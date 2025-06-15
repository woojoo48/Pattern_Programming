package commands;

import java.util.Vector;
import shapes.GShape;

public class PasteCommand implements Command {
    private Vector<GShape> allShapes;
    private Vector<GShape> clipboard;
    private Vector<GShape> pastedShapes;
    private static final int PASTE_OFFSET = 20;
    
    public PasteCommand(Vector<GShape> allShapes, Vector<GShape> clipboard) {
        this.allShapes = allShapes;
        this.clipboard = clipboard;
        this.pastedShapes = new Vector<>();
    }
    
    @Override
    public void execute() {
        pastedShapes.clear();
        
        for (GShape shape : clipboard) {
            GShape cloned = shape.clone();
            cloned.getAffineTransform().translate(PASTE_OFFSET, PASTE_OFFSET);
            
            allShapes.add(cloned);
            pastedShapes.add(cloned);
        }
    }
    
    @Override
    public void undo() {
        for (GShape shape : pastedShapes) {
            allShapes.remove(shape);
        }
    }

}