package transformers;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import shapes.GShape;

public class GRotater extends GTransformer {
    
    private GShape shape;
    private double cx, cy;  
    private double op;
    
    public GRotater(GShape shape) {
        super(shape);
        this.shape = shape;
    }

    @Override
    public void start(Graphics2D g2d, int x, int y) {
        if (shape.isGrouped() && groupShapes != null) {
            // ✨ 그룹화된 경우 그룹 전체의 중심점 계산
            Rectangle groupBounds = getGroupBounds(shape.getGroupId());
            if (groupBounds != null) {
                this.cx = groupBounds.getCenterX();
                this.cy = groupBounds.getCenterY();
            } else {
                // Fallback: 개별 도형 중심점
                Rectangle bounds = this.shape.getBounds();
                this.cx = bounds.getCenterX();
                this.cy = bounds.getCenterY();
            }
        } else {
            // 개별 도형의 중심점
            Rectangle bounds = this.shape.getBounds();
            this.cx = bounds.getCenterX();
            this.cy = bounds.getCenterY();
        }

        this.op = Math.atan2(y - cy, x - cx);
    }

    @Override
    public void drag(Graphics2D g2d, int x, int y) {
        double rp = Math.atan2(y - cy, x - cx);
        double dp = rp - op;

        // 각도 점프 방지
        if (dp > Math.PI) {
            dp -= 2 * Math.PI;
        } else if (dp < -Math.PI) {
            dp += 2 * Math.PI;
        }
        
        if (shape.isGrouped() && groupShapes != null) {
            // ✨ 그룹 전체 회전
            rotateGroup(shape.getGroupId(), dp);
        } else {
            // 개별 도형 회전
            rotateSingleShape(shape, dp);
        }
        
        this.op = rp;
    }
    
    // ✨ 개별 도형 회전
    private void rotateSingleShape(GShape shape, double angle) {
        shape.getAffineTransform().translate(cx, cy);
        shape.getAffineTransform().rotate(angle);
        shape.getAffineTransform().translate(-cx, -cy);
    }
    
    // ✨ 그룹 전체 회전
    private void rotateGroup(int groupId, double angle) {
        // 그룹의 모든 도형에 같은 회전 적용
        for (GShape s : groupShapes) {
            if (s.getGroupId() == groupId) {
                s.getAffineTransform().translate(cx, cy);
                s.getAffineTransform().rotate(angle);
                s.getAffineTransform().translate(-cx, -cy);
            }
        }
    }
    
    // ✨ 그룹 전체 경계 계산
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
    public void finish(Graphics2D g2d, int x, int y) {
    }

    @Override
    public void addPoint(Graphics2D graphics, int x, int y) {
    }
}