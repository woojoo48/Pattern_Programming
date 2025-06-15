package commands;

import java.util.Vector;

import shapes.GShape;

public class UngroupCommand implements Command {
    private Vector<GShape> allShapes;
    private Vector<GShape> groupedShapes;
    private int originalGroupId;
    
    public UngroupCommand(Vector<GShape> allShapes, int groupId) {
        this.allShapes = allShapes;
        this.originalGroupId = groupId;
        
        this.groupedShapes = new Vector<>();
        for (GShape shape : allShapes) {
            if (shape.getGroupId() == groupId) {
                groupedShapes.add(shape);
            }
        }
    }
    
    @Override
    public void execute() {
        for (GShape shape : groupedShapes) {
            shape.setGroupId(-1);
            shape.setSelected(true);
        }
    }
    
    @Override
    public void undo() {
        for (GShape shape : groupedShapes) {
            shape.setGroupId(originalGroupId);
            shape.setSelected(true);
        }
    }

}