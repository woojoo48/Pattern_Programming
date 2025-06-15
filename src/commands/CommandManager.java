package commands;

import java.util.ArrayList;
import java.util.List;

import global.GConstants;

public class CommandManager {
    private List<Command> history;
    private int currentIndex;
    
    public CommandManager() {
        this.history = new ArrayList<>();
        this.currentIndex = -1;
    }

    public void executeCommand(Command command) {
        command.execute();
        
        addToHistory(command);

    }

    public void addToHistory(Command command) {
        while (history.size() > currentIndex + 1) {
            history.remove(history.size() - 1);
        }
        
        history.add(command);
        currentIndex++;
        
        int maxHistory = GConstants.getMaxHistorySize();
        if (history.size() > maxHistory) {
            history.remove(0);
            currentIndex--;
        }

    }

    public boolean undo() {
        if (!canUndo()) {
            return false;
        }
        
        Command command = history.get(currentIndex);
        command.undo();
        currentIndex--;

        return true;
    }

    public boolean redo() {
        if (!canRedo()) {
            return false;
        }
        
        currentIndex++;
        Command command = history.get(currentIndex);
        command.execute();

        return true;
    }
    
    public boolean canUndo() {
        return currentIndex >= 0;
    }
    

    public boolean canRedo() {
        return currentIndex < history.size() - 1;
    }

    public void clear() {
        history.clear();
        currentIndex = -1;
    }

}