package transformers;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import shapes.GShape;
import shapes.GShape.EAnchor;

public class GResizer extends GTransformer{

	private GShape shape;
	private	EAnchor eResizeAnchor;
	private int px, py;
	private int cx, cy;
	
	public GResizer(GShape shape) {
		super(shape);
		this.shape = shape;
		this.eResizeAnchor = null;
	}

	@Override
	public void start(Graphics2D g2d, int x, int y) {
		this.px = x; 
		this.py = y;
		Rectangle r = this.shape.getBounds();

		EAnchor eSelectedAnchor = this.shape.getESeletedAnchor();
		
		// ✨ eResizeAnchor는 항상 설정 (그룹화 여부와 관계없이)
 		switch(eSelectedAnchor) {
		case eNW: eResizeAnchor = EAnchor.eSE; cx=r.x+r.width; 		cy= r.y+r.height; 	break;
		case eWW: eResizeAnchor = EAnchor.eEE; cx=r.x+r.width; 		cy= r.y+r.height/2;	break;
		case eSW: eResizeAnchor = EAnchor.eNE; cx=r.x+r.width; 		cy= r.y; 			break;
		case eSS: eResizeAnchor = EAnchor.eNN; cx=r.x+r.width/2;	cy= r.y; 			break;
		case eSE: eResizeAnchor = EAnchor.eNW; cx=r.x; 				cy= r.y; 			break;
		case eEE: eResizeAnchor = EAnchor.eWW; cx=r.x; 				cy= r.y+r.height/2; break;
		case eNE: eResizeAnchor = EAnchor.eSW; cx=r.x; 				cy= r.y+r.height; 	break;
		case eNN: eResizeAnchor = EAnchor.eSS; cx=r.x+r.width/2; 	cy= r.y+r.height; 	break;
		default:
			break;
		}
		
		// ✨ 그룹화된 경우 중심점(cx, cy)만 다시 계산 (eResizeAnchor는 이미 설정됨)
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

	@Override
	public void drag(Graphics2D g2d, int x, int y) {
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
		default:
			break;
		}
		
		if (shape.isGrouped() && groupShapes != null) {
			// ✨ 그룹 전체 크기 조정
			resizeGroup(shape.getGroupId(), dx, dy);
		} else {
			// 개별 도형 크기 조정
			resizeSingleShape(shape, dx, dy);
		}

		px = x;
		py = y;
	}
	
	// ✨ 개별 도형 크기 조정
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
	
	// ✨ 그룹 전체 크기 조정
	private void resizeGroup(int groupId, double dx, double dy) {
		Rectangle groupBounds = getGroupBounds(groupId);
		if (groupBounds == null) return;
		
		double w1 = groupBounds.width;
		double w2 = dx + w1;
		double h1 = groupBounds.height;
		double h2 = dy + h1;
		
		if (w1 <= 0 || h1 <= 0) return; // 0으로 나누기 방지
		
		double xScale = w2 / w1;
		double yScale = h2 / h1;
		
		// 그룹의 모든 도형에 같은 스케일 적용
		for (GShape s : groupShapes) {
			if (s.getGroupId() == groupId) {
				s.getAffineTransform().translate(cx, cy);
				s.getAffineTransform().scale(xScale, yScale);
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