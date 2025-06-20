package shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

import global.GConstants;

public abstract class GShape implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public enum EPoints{
		e2P,
		eNP
	}
	
	public enum EStrokeStyle {
		SOLID("SOLID"),
		DASHED("DASHED"),
		DOTTED("DOTTED");
		
		private String name;
		
		private EStrokeStyle(String name) {
			this.name = name;
		}
		
		public String getDisplayName() {
			return GConstants.getStrokeStyleLabel(this.name);
		}
		
		public Stroke createStroke(float width) {
			switch (this) {
				case DASHED:
					return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10}, 0);
				case DOTTED:
					return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{3}, 0);
				case SOLID:
				default:
					return new BasicStroke(width);
			}
		}
		
		public static EStrokeStyle fromString(String styleString) {
			for (EStrokeStyle style : values()) {
				if (style.name.equals(styleString)) {
					return style;
				}
			}
			return SOLID;
		}
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
	private int groupId = -1;
	
	private Color strokeColor;
	private Color fillColor;
	private float strokeWidth;
	private EStrokeStyle strokeStyle;
	
	public GShape(Shape shape) {
		this.shape = shape;
		this.affineTransform = new AffineTransform();
		
		this.anchors = new Ellipse2D[EAnchor.values().length-1];
		for(int i = 0;i<this.anchors.length;i++) {
			this.anchors[i] = new Ellipse2D.Double();
		}
		
		this.bSelected = false;
		this.eSelectedAnchor = null;
		
		this.strokeColor = GConstants.getDefaultStrokeColor();
		this.fillColor = GConstants.getDefaultFillColor();
		this.strokeWidth = GConstants.getDefaultStrokeWidth();
		this.strokeStyle = EStrokeStyle.fromString(GConstants.getDefaultStrokeStyle());
	}
	
	public Color getStrokeColor() {
		return this.strokeColor;
	}
	
	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	public Color getFillColor() {
		return this.fillColor;
	}
	
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
	
	public float getStrokeWidth() {
		return this.strokeWidth;
	}
	
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	public EStrokeStyle getStrokeStyle() {
		return this.strokeStyle;
	}
	
	public void setStrokeStyle(EStrokeStyle strokeStyle) {
		this.strokeStyle = strokeStyle;
	}
	
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
	
	public int getGroupId() {
		return this.groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public boolean isGrouped() {
		return this.groupId != -1;
	}
	
	public abstract GShape clone();
	
	protected void copyPropertiesTo(GShape target) {
		target.affineTransform = (AffineTransform) this.affineTransform.clone();
		target.groupId = this.groupId;
		target.bSelected = false;
		// Copy style properties
		target.strokeColor = this.strokeColor;
		target.fillColor = this.fillColor;
		target.strokeWidth = this.strokeWidth;
		target.strokeStyle = this.strokeStyle;
	}
	
	private void setAnchors() {
		Rectangle bounds = this.shape.getBounds();
	    
		int bx = bounds.x;
		int by = bounds.y;
		int bw = bounds.width;
		int bh = bounds.height;
		
		int anchorW = GConstants.getAnchorWidth();
		int anchorH = GConstants.getAnchorHeight();
		int rotationOffset = GConstants.getRotationHandleOffset();
		
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
			case eRR: cx = bx+bw/2; cy = by+rotationOffset;		break;
			default: break;
			}
			anchors[i].setFrame(cx-anchorW/2,cy-anchorH/2,anchorW,anchorH);

		}
	}

	public void draw(Graphics2D graphics2D) {
		Shape transformedShape = this.affineTransform.createTransformedShape(shape);
		
		// Set stroke style and color
		Stroke originalStroke = graphics2D.getStroke();
		Color originalColor = graphics2D.getColor();
		
		// Fill the shape first
		if (fillColor != null) {
			graphics2D.setColor(fillColor);
			graphics2D.fill(transformedShape);
		}
		
		// Draw the outline
		graphics2D.setColor(strokeColor);
		graphics2D.setStroke(strokeStyle.createStroke(strokeWidth));
		graphics2D.draw(transformedShape);
		
		// Restore original graphics settings
		graphics2D.setStroke(originalStroke);
		graphics2D.setColor(originalColor);
		
		if(bSelected) {
		    this.setAnchors();
		    
		    int anchorW = GConstants.getAnchorWidth();
		    int anchorH = GConstants.getAnchorHeight();
		    
		    for(int i = 0;i<this.anchors.length;i++) {
		        double anchorCenterX = anchors[i].getCenterX();
		        double anchorCenterY = anchors[i].getCenterY();
		        
		        Point2D center = new Point2D.Double(anchorCenterX, anchorCenterY);
		        Point2D transformedCenter = this.affineTransform.transform(center, null);
		        
		        Ellipse2D fixedSizeAnchor = new Ellipse2D.Double(
		            transformedCenter.getX() - anchorW/2,
		            transformedCenter.getY() - anchorH/2,
		            anchorW,
		            anchorH
		        );
		        
		        Color penColor = graphics2D.getColor();
		        graphics2D.setColor(GConstants.getAnchorFillColor());
		        graphics2D.fill(fixedSizeAnchor);
		        graphics2D.setColor(penColor);
		        graphics2D.draw(fixedSizeAnchor);
		    }
		}	
	}
	
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
	
	public abstract void setPoint(int x, int y);
	public abstract void addPoint(int x, int y);
	public abstract void dragPoint(int x, int y);
}