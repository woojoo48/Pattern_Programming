package commands;

import java.util.Vector;
import shapes.GShape;

public class DeleteCommand implements Command {
    private Vector<GShape> allShapes;
    private Vector<GShape> deletedShapes;
    private Vector<Integer> originalIndices;
    
    public DeleteCommand(Vector<GShape> allShapes, Vector<GShape> shapesToDelete) {
        this.allShapes = allShapes;
        this.deletedShapes = new Vector<>(shapesToDelete);
        this.originalIndices = new Vector<>();
        
        for (GShape shape : shapesToDelete) {
            originalIndices.add(allShapes.indexOf(shape));
        }
    }
    
    @Override
    public void execute() {
        for (GShape shape : deletedShapes) {
            allShapes.remove(shape);
        }
    }
    
    @Override
    public void undo() {
        for (int i = 0; i < deletedShapes.size(); i++) {
            GShape shape = deletedShapes.get(i);
            int originalIndex = originalIndices.get(i);
            
            if (originalIndex >= 0 && originalIndex <= allShapes.size()) {
                allShapes.add(originalIndex, shape);
            } else {
                allShapes.add(shape);
            }
        }
    }

}