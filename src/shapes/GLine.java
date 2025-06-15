package shapes;

import java.awt.geom.Line2D;

public class GLine extends GShape {
	private static final long serialVersionUID = 1L;
	private Line2D.Float line;

    public GLine() {
        super(new Line2D.Float(0, 0, 0, 0));
        this.line = (Line2D.Float) this.getShape();
    }

    @Override
    public void setPoint(int x, int y) {
        this.line.setLine(x, y, x, y);
    }

    @Override
    public void dragPoint(int x, int y) {
        float x1 = this.line.x1;
        float y1 = this.line.y1;
        this.line.setLine(x1, y1, x, y);
    }

    @Override
    public void addPoint(int x, int y) {
    }
    
    @Override
    public GShape clone() {
        GLine cloned = new GLine();
        cloned.line.setLine(this.line.x1, this.line.y1, this.line.x2, this.line.y2);
        copyPropertiesTo(cloned);
        return cloned;
    }
}