package transformers;

import java.awt.Graphics2D;
import shapes.GShape;

public class GDrawer extends GTransformer {
	
    private GShape shape;
    
    public GDrawer(GShape shape) {
        super(shape);
        this.shape = shape;
    }
    
    @Override
    public void start(Graphics2D g2D, int x, int y) {
        this.shape.setPoint(x, y);
    }
    
    @Override
    public void drag(Graphics2D g2D, int x, int y) {
        this.shape.dragPoint(x, y);
    }
    
    @Override
    public void finish(Graphics2D g2D, int x, int y) {
    }

	@Override
	public void addPoint(Graphics2D graphics, int x, int y) {
		this.shape.addPoint(x, y);
	}
}