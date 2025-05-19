package shapes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public abstract  class GShape {
	private final static int ANCHOR_W = 10;
	private final static int ANCHOR_H = 10;
	
	public enum EPoints{
		e2P,
		eNP
	}
	
	public enum EAnchor{
		eSS(new Cursor(Cursor.S_RESIZE_CURSOR)),
		eSE(new Cursor(Cursor.SE_RESIZE_CURSOR)),
		eSW(new Cursor(Cursor.SW_RESIZE_CURSOR)),
		eNN(new Cursor(Cursor.N_RESIZE_CURSOR)),
		eNE(new Cursor(Cursor.NE_RESIZE_CURSOR)),
		eNW(new Cursor(Cursor.NW_RESIZE_CURSOR)),
		eEE(new Cursor(Cursor.E_RESIZE_CURSOR)),
		eWW(new Cursor(Cursor.W_RESIZE_CURSOR)),
		eRR(new Cursor(Cursor.HAND_CURSOR)),
		eMM(new Cursor(Cursor.MOVE_CURSOR));
		
		private Cursor cursor;
		private EAnchor(Cursor cursor) {
			this.cursor = cursor;
		}
		
		public Cursor getCursor() {
			return this.cursor;
		}
	}
	
	private Shape shape;
	private Ellipse2D anchors[];
	private boolean bSelected;
	private EAnchor eSelectedAnchor;
	private AffineTransform affineTransform;
	private int px,py;
	
	public GShape(Shape shape) {
		this.shape = shape;
		this.affineTransform = new AffineTransform();
		
		this.anchors = new Ellipse2D[EAnchor.values().length-1];
		for(int i = 0;i<this.anchors.length;i++) {
			this.anchors[i] = new Ellipse2D.Double();
		}
		
		this.bSelected = false;
		this.eSelectedAnchor = null;
	}
	
	protected Shape getShape() {
		return this.shape;
	}
	
	public void setSelected(boolean bSelected) {
		this.bSelected = bSelected;
	}
	
	public boolean isSelected() {
		return this.bSelected;
	}
	
	public EAnchor getESeletedAnchor() {
		return this.eSelectedAnchor;
	}
	
	//methods
		private void setAnchors() {
			Rectangle bounds = this.shape.getBounds();
		    
			int bx = bounds.x;
			int by = bounds.y;
			int bw = bounds.width;
			int bh = bounds.height;
			
			int cx=0;
			int cy=0;
			for(int i = 0;i<this.anchors.length;i++) {
				switch(EAnchor.values()[i]) {
				case eSS: cx = bx+bw/2; cy = by+bh; 	break;
				case eSE: cx = bx+bw; 	cy = by+bh; 	break;
				case eSW: cx = bx; 		cy = by+bh; 	break;
				case eNN: cx = bx+bw/2; cy = by; 		break;
				case eNE: cx = bx+bw; 	cy = by; 		break;
				case eNW: cx = bx; 		cy = by; 		break;
				case eEE: cx = bx+bw; 	cy = by+bh/2; 	break;
				case eWW: cx = bx; 		cy = by+bh/2; 	break;
				case eRR: cx = bx+bw/2; cy = by-30;		break;
				default: break;
				}
				anchors[i].setFrame(cx-ANCHOR_W/2,cy-ANCHOR_H/2,ANCHOR_W,ANCHOR_H);

			}
		}
	
	public void draw(Graphics2D graphics2D) {
		Shape transformedShape = this.affineTransform.createTransformedShape(shape);
		graphics2D.draw(transformedShape);
		
		if(bSelected) {
				this.setAnchors();
			for(int i = 0;i<this.anchors.length;i++) {
				Shape transformedAnchor = this.affineTransform.createTransformedShape(anchors[i]);
				Color penColor = graphics2D.getColor();
				graphics2D.setColor(graphics2D.getBackground());
				graphics2D.fill(anchors[i]);
				graphics2D.setColor(penColor);
				graphics2D.draw(transformedAnchor);
			}
		}	}
	
	public boolean contains(int x, int y) {
		if(bSelected) {
			for(int i = 0;i<this.anchors.length;i++) {	
				Shape transformedAnchor = this.affineTransform.createTransformedShape(anchors[i]);
				if(transformedAnchor.contains(x,y)) {
					this.eSelectedAnchor = EAnchor.values()[i];
					return true;
				}
			}	
		}
		Shape transformedShape = this.affineTransform.createTransformedShape(shape);
	    if(transformedShape.contains(x,y)) {
	        this.eSelectedAnchor = EAnchor.eMM;
	        return true;
	    }
	    return false;
	}
	
	//translation
	public void setMovePoint(int x, int y) {
			this.px = x;
			this.py = y;
	}

	public void movePoint(int x, int y) {
			int dx = x - px;
			int dy = y - py;
			
			this.affineTransform.translate(dx, dy);

			this.px = x;
			this.py = y;
	}
	
	//resize
	public void setResizePoint(int x, int y) {
		this.px = x;
		this.py = y;
	}
	
	public void resizePoint(int x, int y) {
	    EAnchor anchor = this.eSelectedAnchor;
	    Rectangle bounds = this.shape.getBounds();

	    double refX = 0, refY = 0;
	    
	    switch(anchor) {
	        case eSE: refX = bounds.x; refY = bounds.y; break;
	        case eSW: refX = bounds.x + bounds.width; refY = bounds.y; break;
	        case eNE: refX = bounds.x; refY = bounds.y + bounds.height; break;
	        case eNW: refX = bounds.x + bounds.width; refY = bounds.y + bounds.height; break;
	        case eNN: refX = bounds.x + bounds.width/2; refY = bounds.y + bounds.height; break;
	        case eSS: refX = bounds.x + bounds.width/2; refY = bounds.y; break;
	        case eEE: refX = bounds.x; refY = bounds.y + bounds.height/2; break;
	        case eWW: refX = bounds.x + bounds.width; refY = bounds.y + bounds.height/2; break;
	        default: return; 
	    }
	    
	    double newWidth = bounds.width;
	    double newHeight = bounds.height;
	    
	    switch(anchor) {
	        case eSE: 
	            newWidth = x - bounds.x;
	            newHeight = y - bounds.y;
	            break;
	        case eSW: 
	            newWidth = (bounds.x + bounds.width) - x;
	            newHeight = y - bounds.y;
	            break;
	        case eNE: 
	            newWidth = x - bounds.x;
	            newHeight = (bounds.y + bounds.height) - y;
	            break;
	        case eNW: 
	            newWidth = (bounds.x + bounds.width) - x;
	            newHeight = (bounds.y + bounds.height) - y;
	            break;
	        case eNN: 
	            newHeight = (bounds.y + bounds.height) - y;
	            break;
	        case eSS: 
	            newHeight = y - bounds.y;
	            break;
	        case eEE: 
	            newWidth = x - bounds.x;
	            break;
	        case eWW:
	            newWidth = (bounds.x + bounds.width) - x;
	            break;
	    }
	    
	    newWidth = Math.max(newWidth, 1);
	    newHeight = Math.max(newHeight, 1);

	    double scaleX = newWidth / bounds.width;
	    double scaleY = newHeight / bounds.height;

	    AffineTransform at = new AffineTransform();
	    at.translate(refX, refY);
	    at.scale(scaleX, scaleY);
	    at.translate(-refX, -refY);

	    this.affineTransform = at;
	    
	    this.px = x;
	    this.py = y;
	}
	
	//draw함수
	public abstract void setPoint(int x, int y);
	public abstract void addPoint(int x, int y);
	public abstract void dragPoint(int x, int y);

	public void translate(int tx, int ty) {
		this.affineTransform.translate(tx, ty);		
	}
}
