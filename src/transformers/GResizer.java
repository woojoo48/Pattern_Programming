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
		Shape transformedShape = this.shape.getTransformedShape();
		double w1 = transformedShape.getBounds().width;
		double w2 = dx+w1;
		double h1 = transformedShape.getBounds().height;
		double h2 = dy + h1; 

		double xScale = w2/w1;
		double yScale = h2/h1;
		
		this.shape.getAffineTransform().translate(cx,cy);
		this.shape.getAffineTransform().scale(xScale,yScale);
		this.shape.getAffineTransform().translate(-cx,-cy);

		px = x;
		py = y;
	}

	@Override
	public void finish(Graphics2D g2d, int x, int y) {
		
	}

	@Override
	public void addPoint(Graphics2D graphics, int x, int y) {		
	}

}
