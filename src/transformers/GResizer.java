package transformers;

import java.awt.Graphics2D;

import shapes.GShape;

public class GResizer extends GTransformer{

	private GShape shape;
	
	public GResizer(GShape shape) {
		super(shape);
		this.shape = shape;
	}

	@Override
	public void start(Graphics2D g2d, int x, int y) {
		shape.setResizePoint(x, y);
	}

	@Override
	public void drag(Graphics2D g2d, int x, int y) {
		shape.resizePoint(x, y);
	}

	@Override
	public void finish(Graphics2D g2d, int x, int y) {
		
	}

	@Override
	public void addPoint(Graphics2D graphics, int x, int y) {		
	}

}
