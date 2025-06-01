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
		AffineTransform affineTransform = this.shape.getAffineTransform();
		
		int dx = x - px;
		int dy = y - py;
		
		affineTransform.translate(dx, dy);

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
