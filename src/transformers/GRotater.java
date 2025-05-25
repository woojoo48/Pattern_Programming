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
        Rectangle bounds = this.shape.getBounds();
        //도형 원점 설정. 원점을 기준으로 각도 계산
        this.cx = bounds.getCenterX();
        this.cy = bounds.getCenterY();

        this.op = Math.atan2(y - cy, x - cx);
    }

    @Override
    public void drag(Graphics2D g2d, int x, int y) {
        double rp = Math.atan2(y - cy, x - cx);

        double dp = rp - op;

        //rotate시 각도 점프 방지. 그러나 굳이 필요할까?
//        if (dp > Math.PI) {
//            dp -= 2 * Math.PI;
//        } else if (dp < -Math.PI) {
//            dp += 2 * Math.PI;
//        }
        
        // 이동-회전-역이동 방식으로 회전 적용
        //원점을 도형의 중앙으로 이동
        this.shape.getAffineTransform().translate(cx, cy);
        this.shape.getAffineTransform().rotate(dp);
        //원점 복귀
        this.shape.getAffineTransform().translate(-cx, -cy);
        
        this.op = rp;
    }

    @Override
    public void finish(Graphics2D g2d, int x, int y) {
    }

    @Override
    public void addPoint(Graphics2D graphics, int x, int y) {
    }
}