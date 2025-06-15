package commands;

import java.util.Vector;
import shapes.GShape;

public class GroupCommand implements Command {
    private Vector<GShape> allShapes;
    private Vector<GShape> targetShapes;
    private int groupId;
    private Vector<Integer> originalGroupIds;
    
    public GroupCommand(Vector<GShape> allShapes, Vector<GShape> targetShapes, int groupId) {
        this.allShapes = allShapes;
        this.targetShapes = new Vector<>(targetShapes);
        this.groupId = groupId;
        
        this.originalGroupIds = new Vector<>();
        for (GShape shape : targetShapes) {
            originalGroupIds.add(shape.getGroupId());
        }
    }
    
    @Override
    public void execute() {
        for (GShape shape : targetShapes) {
            shape.setGroupId(groupId);
            shape.setSelected(false);
        }
        
        for (GShape shape : targetShapes) {
            shape.setSelected(true);
        }
    }
    
    @Override
    public void undo() {
        for (int i = 0; i < targetShapes.size(); i++) {
            targetShapes.get(i).setGroupId(originalGroupIds.get(i));
            targetShapes.get(i).setSelected(true);
        }
    }
}