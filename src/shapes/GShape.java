package shapes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

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
	//Getter and Setter
	public AffineTransform getAffineTransform() {
		return this.affineTransform;
	}
	
	public Shape getTransformedShape() {
		return this.affineTransform.createTransformedShape(this.shape);
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
	
	public Rectangle getBounds() {
		return this.shape.getBounds();
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
		        // 앵커 중심점을 변형 적용
		        double anchorCenterX = anchors[i].getCenterX();
		        double anchorCenterY = anchors[i].getCenterY();
		        
		        Point2D center = new Point2D.Double(anchorCenterX, anchorCenterY);
		        Point2D transformedCenter = this.affineTransform.transform(center, null);
		        
		        // 변형된 위치에 원본 크기 앵커 생성
		        Ellipse2D fixedSizeAnchor = new Ellipse2D.Double(
		            transformedCenter.getX() - ANCHOR_W/2,
		            transformedCenter.getY() - ANCHOR_H/2,
		            ANCHOR_W,
		            ANCHOR_H
		        );
		        
		        // 그리기
		        Color penColor = graphics2D.getColor();
		        graphics2D.setColor(Color.WHITE);
		        graphics2D.fill(fixedSizeAnchor);
		        graphics2D.setColor(penColor);
		        graphics2D.draw(fixedSizeAnchor);
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
	
	public boolean contains(GShape shape) {
		return this.shape.contains(shape.getShape().getBounds());
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
	
	//draw method
	public abstract void setPoint(int x, int y);
	public abstract void addPoint(int x, int y);
	public abstract void dragPoint(int x, int y);

	public void translate(int tx, int ty) {
		this.affineTransform.translate(tx, ty);		
	}
}
