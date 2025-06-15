package commands;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private List<Command> history;
    private int currentIndex;
    private static final int MAX_HISTORY = 100;
    
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
        
        if (history.size() > MAX_HISTORY) {
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