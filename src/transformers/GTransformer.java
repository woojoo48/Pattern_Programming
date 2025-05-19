package transformers;

import java.awt.Graphics2D;
import shapes.GShape;

public abstract class GTransformer {
    protected GShape shape;
    
    public GTransformer(GShape shape) {
    	this.shape = shape;
    }
    
    public abstract void start(Graphics2D g2D, int x, int y); 
    
    public abstract void drag(Graphics2D g2D, int x, int y);
    
    public abstract void finish(Graphics2D g2D, int x, int y);
    
	public abstract void addPoint(Graphics2D graphics, int x, int y);
}