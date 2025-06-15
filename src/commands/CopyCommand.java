package commands;

import java.util.Vector;
import shapes.GShape;

public class CopyCommand implements Command {
    private Vector<GShape> clipboard;
    private Vector<GShape> copiedShapes;
    
    public CopyCommand(Vector<GShape> clipboard, Vector<GShape> shapesToCopy) {
        this.clipboard = clipboard;
        this.copiedShapes = new Vector<>(shapesToCopy);
    }
    
    @Override
    public void execute() {
        clipboard.clear();
        for (GShape shape : copiedShapes) {
            clipboard.add(shape.clone());
        }
    }
    
    @Override
    public void undo() {
    }
}