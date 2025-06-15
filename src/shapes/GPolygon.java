package shapes;

import java.awt.Polygon;

public class GPolygon extends GShape{
	private static final long serialVersionUID = 1L;
	private Polygon polygon;
	
	public GPolygon() {
		super(new Polygon());
		this.polygon = (Polygon) this.getShape();
	}
	
	public void setPoint(int x, int y) {
		this.polygon.addPoint(x, y);
		this.polygon.addPoint(x, y); 
	}
	
	public void dragPoint(int x, int y) {
		this.polygon.xpoints[this.polygon.npoints-1] = x;
		this.polygon.ypoints[this.polygon.npoints-1] = y;
	}

	@Override
	public void addPoint(int x, int y) {
		this.polygon.addPoint(x, y); 
	}
	
	@Override
	public GShape clone() {
		GPolygon cloned = new GPolygon();
		cloned.polygon = new Polygon();
		for (int i = 0; i < this.polygon.npoints; i++) {
			cloned.polygon.addPoint(this.polygon.xpoints[i], this.polygon.ypoints[i]);
		}
		copyPropertiesTo(cloned);
		return cloned;
	}
}