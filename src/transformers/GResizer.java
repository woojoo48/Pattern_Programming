package transformers;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import shapes.GShape;
import shapes.GShape.EAnchor;

public class GResizer extends GTransformer{

    private GShape shape;
    private EAnchor eResizeAnchor;
    private int px, py;
    private int cx, cy;
    
    private Map<GShape, AffineTransform> beforeTransforms;
    private Map<GShape, AffineTransform> afterTransforms;
    
    public GResizer(GShape shape) {
        super(shape);
        this.shape = shape;
        this.eResizeAnchor = null;
        this.beforeTransforms = new HashMap<>();
        this.afterTransforms = new HashMap<>();
    }

    @Override
    public void start(Graphics2D g2d, int x, int y) {
        this.px = x; 
        this.py = y;
        
        saveBeforeStates();
        
        Rectangle r = this.shape.getBounds();
        EAnchor eSelectedAnchor = this.shape.getESeletedAnchor();
        
        switch(eSelectedAnchor) {
        case eNW: eResizeAnchor = EAnchor.eSE; cx=r.x+r.width; 		cy= r.y+r.height; 	break;
        case eWW: eResizeAnchor = EAnchor.eEE; cx=r.x+r.width; 		cy= r.y+r.height/2;	break;
        case eSW: eResizeAnchor = EAnchor.eNE; cx=r.x+r.width; 		cy= r.y; 			break;
        case eSS: eResizeAnchor = EAnchor.eNN; cx=r.x+r.width/2;	cy= r.y; 			break;
        case eSE: eResizeAnchor = EAnchor.eNW; cx=r.x; 				cy= r.y; 			break;
        case eEE: eResizeAnchor = EAnchor.eWW; cx=r.x; 				cy= r.y+r.height/2; break;
        case eNE: eResizeAnchor = EAnchor.eSW; cx=r.x; 				cy= r.y+r.height; 	break;
        case eNN: eResizeAnchor = EAnchor.eSS; cx=r.x+r.width/2; 	cy= r.y+r.height; 	break;
        default: break;
        }
        
        if (shape.isGrouped() && groupShapes != null) {
            Rectangle groupBounds = getGroupBounds(shape.getGroupId());
            if (groupBounds != null) {
                switch(eSelectedAnchor) {
                case eNW: cx = groupBounds.x + groupBounds.width; 	cy = groupBounds.y + groupBounds.height; break;
                case eWW: cx = groupBounds.x + groupBounds.width; 	cy = groupBounds.y + groupBounds.height/2; break;
                case eSW: cx = groupBounds.x + groupBounds.width; 	cy = groupBounds.y; break;
                case eSS: cx = groupBounds.x + groupBounds.width/2; cy = groupBounds.y; break;
                case eSE: cx = groupBounds.x; 						cy = groupBounds.y; break;
                case eEE: cx = groupBounds.x; 						cy = groupBounds.y + groupBounds.height/2; break;
                case eNE: cx = groupBounds.x; 						cy = groupBounds.y + groupBounds.height; break;
                case eNN: cx = groupBounds.x + groupBounds.width/2; cy = groupBounds.y + groupBounds.height; break;
                default: break;
                }
            }
        }
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
        if (eResizeAnchor == null) {
            return;
        }
        
        double dx = 0;
        double dy = 0;
        
        switch (eResizeAnchor) {
        case eNW: dx = (x-px); 		dy = (y-py);	break;
        case eWW: dx = (x-px); 		dy = 0;			break;
        case eSW: dx = (x-px); 		dy = -(y-py); 	break;
        case eSS: dx = 0;			dy = -(y-py); 	break;
        case eSE: dx = -(x-px); 	dy = -(y-py);	break;
        case eEE: dx = -(x-px); 	dy = 0; 		break;
        case eNE: dx = -(x-px); 	dy = (y-py);	break;
        case eNN: dx = 0; 			dy = (y-py); 	break;
        default: break;
        }
        
        if (shape.isGrouped() && groupShapes != null) {
            resizeGroup(shape.getGroupId(), dx, dy);
        } else {
            resizeSingleShape(shape, dx, dy);
        }

        px = x;
        py = y;
    }
    
    @Override
    protected void finishTransform(Graphics2D g2d, int x, int y) {
        saveAfterStates();
    }
    
    @Override
    protected boolean shouldSaveToHistory() {
        return !beforeTransforms.isEmpty() && !afterTransforms.isEmpty();
    }
    
    private void resizeSingleShape(GShape shape, double dx, double dy) {
        Shape transformedShape = shape.getTransformedShape();
        double w1 = transformedShape.getBounds().width;
        double w2 = dx + w1;
        double h1 = transformedShape.getBounds().height;
        double h2 = dy + h1; 

        double xScale = w2/w1;
        double yScale = h2/h1;
        
        shape.getAffineTransform().translate(cx, cy);
        shape.getAffineTransform().scale(xScale, yScale);
        shape.getAffineTransform().translate(-cx, -cy);
    }
    
    private void resizeGroup(int groupId, double dx, double dy) {
        Rectangle groupBounds = getGroupBounds(groupId);
        if (groupBounds == null) return;
        
        double w1 = groupBounds.width;
        double w2 = dx + w1;
        double h1 = groupBounds.height;
        double h2 = dy + h1;
        
        if (w1 <= 0 || h1 <= 0) return;
        
        double xScale = w2 / w1;
        double yScale = h2 / h1;
        
        for (GShape s : groupShapes) {
            if (s.getGroupId() == groupId) {
                s.getAffineTransform().translate(cx, cy);
                s.getAffineTransform().scale(xScale, yScale);
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