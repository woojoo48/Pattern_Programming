package transformers;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import shapes.GShape;

public class GRotater extends GTransformer {
    
    private GShape shape;
    private double cx, cy;  
    private double op;
    
    private Map<GShape, AffineTransform> beforeTransforms;
    private Map<GShape, AffineTransform> afterTransforms;
    
    public GRotater(GShape shape) {
        super(shape);
        this.shape = shape;
        this.beforeTransforms = new HashMap<>();
        this.afterTransforms = new HashMap<>();
    }

    @Override
    public void start(Graphics2D g2d, int x, int y) {
        saveBeforeStates();
        
        if (shape.isGrouped() && groupShapes != null) {
            Rectangle groupBounds = getGroupBounds(shape.getGroupId());
            if (groupBounds != null) {
                this.cx = groupBounds.getCenterX();
                this.cy = groupBounds.getCenterY();
            } else {
                Rectangle bounds = this.shape.getBounds();
                this.cx = bounds.getCenterX();
                this.cy = bounds.getCenterY();
            }
        } else {
            Rectangle bounds = this.shape.getBounds();
            this.cx = bounds.getCenterX();
            this.cy = bounds.getCenterY();
        }

        this.op = Math.atan2(y - cy, x - cx);
    }
    
    private void saveBeforeStates() {
        beforeTransforms.clear();
        
        if (shape.isGrouped() && groupShapes != null) {
            int groupId = shape.getGroupId();
            for (GShape s : groupShapes) {
                if (s.getGroupId() == groupId) {
                    beforeTransforms.put(s, (AffineTransform) s.getAffineTransform().clone());
                }
            }
        } else {
            beforeTransforms.put(shape, (AffineTransform) shape.getAffineTransform().clone());
        }
    }
    
    private void saveAfterStates() {
        afterTransforms.clear();
        
        if (shape.isGrouped() && groupShapes != null) {
            int groupId = shape.getGroupId();
            for (GShape s : groupShapes) {
                if (s.getGroupId() == groupId) {
                    afterTransforms.put(s, (AffineTransform) s.getAffineTransform().clone());
                }
            }
        } else {
            afterTransforms.put(shape, (AffineTransform) shape.getAffineTransform().clone());
        }
    }

    @Override
    public void drag(Graphics2D g2d, int x, int y) {
        if (beforeTransforms.isEmpty()) {
            return;
        }
        
        double rp = Math.atan2(y - cy, x - cx);
        double dp = rp - op;

        if (dp > Math.PI) {
            dp -= 2 * Math.PI;
        } else if (dp < -Math.PI) {
            dp += 2 * Math.PI;
        }
        
        if (shape.isGrouped() && groupShapes != null) {
            rotateGroup(shape.getGroupId(), dp);
        } else {
            rotateSingleShape(shape, dp);
        }
        
        this.op = rp;
    }
    
    @Override
    protected void finishTransform(Graphics2D g2d, int x, int y) {
        saveAfterStates();
    }
    
    @Override
    protected boolean shouldSaveToHistory() {
        return !beforeTransforms.isEmpty() && !afterTransforms.isEmpty();
    }
    
    private void rotateSingleShape(GShape shape, double angle) {
        shape.getAffineTransform().translate(cx, cy);
        shape.getAffineTransform().rotate(angle);
        shape.getAffineTransform().translate(-cx, -cy);
    }
    
    private void rotateGroup(int groupId, double angle) {
        for (GShape s : groupShapes) {
            if (s.getGroupId() == groupId) {
                s.getAffineTransform().translate(cx, cy);
                s.getAffineTransform().rotate(angle);
                s.getAffineTransform().translate(-cx, -cy);
            }
        }
    }
    
    private Rectangle getGroupBounds(int groupId) {
        Rectangle bounds = null;
        for (GShape s : groupShapes) {
            if (s.getGroupId() == groupId) {
                Rectangle shapeBounds = s.getTransformedShape().getBounds();
                if (bounds == null) {
                    bounds = new Rectangle(shapeBounds);
                } else {
                    bounds = bounds.union(shapeBounds);
                }
            }
        }
        return bounds;
    }

    @Override
    public void addPoint(Graphics2D graphics, int x, int y) {
    }
    
    @Override
    public void execute() {
        for (Map.Entry<GShape, AffineTransform> entry : afterTransforms.entrySet()) {
            entry.getKey().getAffineTransform().setTransform(entry.getValue());
        }
    }
    
    @Override
    public void undo() {
        for (Map.Entry<GShape, AffineTransform> entry : beforeTransforms.entrySet()) {
            entry.getKey().getAffineTransform().setTransform(entry.getValue());
        }
    }
    
}