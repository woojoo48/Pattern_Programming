package transformers;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import shapes.GShape;

public class GMover extends GTransformer {
	
	private GShape shape;
	private int px, py;
	
	public GMover(GShape shape) {
		super(shape);
		this.shape = shape;
	}

	@Override
	public void start(Graphics2D g2d, int x, int y) {
		this.px = x;
		this.py = y;
	}

	@Override
	public void drag(Graphics2D g2d, int x, int y) {
		int dx = x - px;
		int dy = y - py;
		
		if (shape.isGrouped() && groupShapes != null) {
			// ✨ 그룹 전체 이동
			int groupId = shape.getGroupId();
			for (GShape s : groupShapes) {
				if (s.getGroupId() == groupId) {
					s.getAffineTransform().translate(dx, dy);
				}
			}
		} else {
			// 개별 도형 이동
			AffineTransform affineTransform = this.shape.getAffineTransform();
			affineTransform.translate(dx, dy);
		}

		this.px = x;
		this.py = y;
	}

	@Override
	public void finish(Graphics2D g2d, int x, int y) {
		
	}

	@Override
	public void addPoint(Graphics2D graphics, int x, int y) {
	}
}