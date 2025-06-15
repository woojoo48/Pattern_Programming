package shapes;

import java.awt.geom.Rectangle2D;

public class GRectangle extends GShape{
	private static final long serialVersionUID = 1L;
	private Rectangle2D rectangle;
	
	public GRectangle() {
		super(new Rectangle2D.Float(0,0,0,0));
		this.rectangle = (Rectangle2D) this.getShape();
	}
	
	public void setPoint(int x, int y) {
		this.rectangle.setFrame(x,y,0,0);
	}
	
	public void dragPoint(int x, int y) {
		double ox = rectangle.getX();
		double oy = rectangle.getY();
		double w = x-ox;
		double h = y-oy;

		this.rectangle.setFrame(ox, oy,w,h);
	}

	@Override
	public void addPoint(int x, int y) {
	}
	
	@Override
	public GShape clone() {
		GRectangle cloned = new GRectangle();
		cloned.rectangle.setFrame(this.rectangle.getFrame());
		copyPropertiesTo(cloned);
		return cloned;
	}
}