package frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JPanel;
import frames.GShapeToolBar.EShapeTool;
import shapes.GShape;
import shapes.GShape.EAnchor;
import shapes.GShape.EPoints;
import transformers.GDrawer;
import transformers.GMover;
import transformers.GResizer;
import transformers.GTransformer;

public class GDrawingPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    public enum EDrawingState {
        eIdle,
        e2P,
        eNP
    }
    
    private Vector<GShape> shapes;
    private GTransformer transformer;
    private GShape currentShape;
    private GShape selectedShape;
    
    //constraint
    private EShapeTool eShapeTool;
    private EDrawingState eDrawingState;
    
    public GDrawingPanel() {
        this.setBackground(Color.WHITE);
        
        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        
        this.currentShape = null;
        this.selectedShape = null;
        this.shapes = new Vector<GShape>();
        this.eShapeTool = null;
        this.eDrawingState = EDrawingState.eIdle;
    }

    public void initialize() {
        
    }
    
    public void initialize(String shape) {
        repaint();
    }

    public void setEShapeTool(EShapeTool eShapeTool) {
        this.eShapeTool = eShapeTool;
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for(GShape shape: shapes) {
            shape.draw((Graphics2D)g);
        }
    }
    
    private GShape onShape(int x, int y) {
        for(GShape shape: this.shapes) {
            if(shape.contains(x,y)) {
                return shape;
            }
        }
        return null;
    }
    
    private void startTransform(int x, int y) {
		//set shape
		this.currentShape = eShapeTool.newShape();
		this.shapes.add(this.currentShape);
		
		if (this.eShapeTool == EShapeTool.eSelect) {
			this.selectedShape = onShape(x,y);
			if(this.selectedShape == null) {
				this.transformer = new GDrawer(this.currentShape);
			} else if(this.selectedShape.getESeletedAnchor() == EAnchor.eMM){
				this.transformer = new GMover(this.selectedShape);
			} else if(this.selectedShape.getESeletedAnchor() == EAnchor.eRR){
				this.transformer = new GMover(this.selectedShape);
				//추후에 rotater로 바껴야 함
			} else {
				this.transformer = new GResizer(this.selectedShape);
			}
		} else {
			this.transformer = new GDrawer(this.currentShape);
		}
		this.transformer.start((Graphics2D) getGraphics(), x, y);
	}

	private void keepTransform(int x, int y) {
		this.transformer.drag((Graphics2D) getGraphics(), x, y);
		this.repaint();
	}
	
	private void addPoint(int x, int y) {
		this.transformer.addPoint((Graphics2D) getGraphics(), x, y);

	}
	
	private void finishTransform(int x, int y) {
		this.transformer.finish((Graphics2D) getGraphics(), x, y);
		this.selectShape(this.currentShape);
		
		if(this.eShapeTool == EShapeTool.eSelect) {
			this.shapes.remove(this.shapes.size()-1);
			for(GShape shape : this.shapes) {
				if(this.currentShape.contains(shape)) {
					shape.setSelected(true);
				} else {
					shape.setSelected(false);
				}
			}
		}
		this.repaint();
	}
	
	private void selectShape(GShape shape) {
	    for(GShape otherShape: this.shapes) {
	        otherShape.setSelected(false);
	    }
	    if(shape != null) {
	        shape.setSelected(true);
	        this.selectedShape = shape;
	    }
	}
	
	private void changeCursor(int x, int y) {
		if(this.eShapeTool == EShapeTool.eSelect) {
			this.selectedShape = onShape(x, y);
			if(this.selectedShape == null) {
				this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} else{
				EAnchor eAnchor = this.selectedShape.getESeletedAnchor();
				this.setCursor(eAnchor.getCursor());
			}
		}
	}
	
	private class MouseHandler implements MouseListener, MouseMotionListener {

		@Override
		public void mouseClicked(MouseEvent e) { 
			if(e.getClickCount() == 1) {
				this.mouse1Clicked(e);
			} else if(e.getClickCount() == 2) {
				this.mouse2Clicked(e);
			} 
		}
	
		private void mouse1Clicked(MouseEvent e) {
			if(eDrawingState == EDrawingState.eIdle) {
				//set Transformer
				if(eShapeTool.getEPoints() == EPoints.e2P) {
						startTransform(e.getX(), e.getY());
	                    eDrawingState = EDrawingState.e2P;				
				} else if(eShapeTool.getEPoints() == EPoints.eNP) {
					startTransform(e.getX(),e.getY());
					eDrawingState = EDrawingState.eNP;
				}
			}else if(eDrawingState == EDrawingState.e2P) {
				finishTransform(e.getX(),e.getY());
				eDrawingState = EDrawingState.eIdle;
			}else if(eDrawingState == EDrawingState.eNP) {
				addPoint(e.getX(),e.getY());
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if(eDrawingState == EDrawingState.e2P) {
				keepTransform(e.getX(),e.getY());		
			} else if(eDrawingState == EDrawingState.eNP) {
				keepTransform(e.getX(),e.getY());		
			} else if(eDrawingState == EDrawingState.eIdle) {
				changeCursor(e.getX(),e.getY());
			}
		}
		
		private void mouse2Clicked(MouseEvent e) {
			if(eDrawingState == EDrawingState.eNP) {
				finishTransform(e.getX(),e.getY());
				eDrawingState = EDrawingState.eIdle;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}
}
