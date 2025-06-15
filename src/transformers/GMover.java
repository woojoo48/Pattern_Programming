package transformers;

import java.awt.Graphics2D;
import shapes.GShape;

public class GMover extends GTransformer {
    
    private GShape shape;
    private int px, py;
    private int totalDx, totalDy;
    
    public GMover(GShape shape) {
        super(shape);
        this.shape = shape;
        this.totalDx = 0;
        this.totalDy = 0;
    }

    @Override
    public void start(Graphics2D g2d, int x, int y) {
        this.px = x;
        this.py = y;
        this.totalDx = 0;
        this.totalDy = 0;
    }

    @Override
    public void drag(Graphics2D g2d, int x, int y) {
        int dx = x - px;
        int dy = y - py;
        
        totalDx += dx;
        totalDy += dy;
        
        performMove(dx, dy);

        this.px = x;
        this.py = y;
    }
    
    private void performMove(int dx, int dy) {
        if (shape.isGrouped() && groupShapes != null) {
            int groupId = shape.getGroupId();
            for (GShape s : groupShapes) {
                if (s.getGroupId() == groupId) {
                    s.getAffineTransform().translate(dx, dy);
                }
            }
        } else {
            shape.getAffineTransform().translate(dx, dy);
        }
    }
    
    @Override
    protected void finishTransform(Graphics2D g2d, int x, int y) {
    }
    
    @Override
    protected boolean shouldSaveToHistory() {
        return Math.abs(totalDx) > 1 || Math.abs(totalDy) > 1;
    }

    @Override
    public void addPoint(Graphics2D graphics, int x, int y) {
    }
    
    @Override
    public void execute() {
        performMove(totalDx, totalDy);
    }
    
    @Override
    public void undo() {
        performMove(-totalDx, -totalDy);
    }
    
}