package transformers;

import java.awt.Graphics2D;
import shapes.GShape;

public class GDrawer extends GTransformer {
    
    private GShape shape;
    private boolean wasAdded = false;
    
    public GDrawer(GShape shape) {
        super(shape);
        this.shape = shape;
    }
    
    @Override
    public void start(Graphics2D g2D, int x, int y) {
        this.shape.setPoint(x, y);
        
        if (groupShapes != null && !groupShapes.contains(shape)) {
            groupShapes.add(shape);
            wasAdded = true;
        }
    }
    
    @Override
    public void drag(Graphics2D g2D, int x, int y) {
        this.shape.dragPoint(x, y);
    }
    
    @Override
    protected void finishTransform(Graphics2D g2D, int x, int y) {
    }
    
    @Override
    protected boolean shouldSaveToHistory() {
        return wasAdded;
    }

    @Override
    public void addPoint(Graphics2D graphics, int x, int y) {
        this.shape.addPoint(x, y);
    }
    
    @Override
    public void execute() {
        if (groupShapes != null && !groupShapes.contains(shape)) {
            groupShapes.add(shape);
        }
    }
    
    @Override
    public void undo() {
        if (groupShapes != null) {
            groupShapes.remove(shape);
        }
    }
    
}