package slideFrame;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import frames.GDrawingPanel;
import shapes.GShape;

public class GSlide implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private transient GDrawingPanel drawingPanel;
    private String name;
    private boolean isModified;
    private Color backgroundColor;
    private Vector<GShape> shapes;
    
    public GSlide() {
        this("새 슬라이드");
    }
    
    public GSlide(String name) {
        this.name = name;
        this.isModified = false;
        this.backgroundColor = Color.WHITE;
        this.shapes = new Vector<GShape>();
        this.createDrawingPanel();
    }
    


    private void createDrawingPanel() {
        this.drawingPanel = new GDrawingPanel();
        this.drawingPanel.initialize();
        this.drawingPanel.setBackground(this.backgroundColor);
        
        if (this.shapes != null) {
            this.drawingPanel.setShapes(this.shapes);
        }
    }
    
    public GDrawingPanel getDrawingPanel() {
        if (this.drawingPanel == null) {
            this.createDrawingPanel();
        }
        return this.drawingPanel;
    }
    
    public Vector<GShape> getShapes() {
        if (this.drawingPanel != null) {
            this.shapes = this.drawingPanel.getShape();
        }
        return this.shapes;
    }
    
    public void setShapes(Vector<GShape> shapes) {
        this.shapes = shapes;
        if (this.drawingPanel != null) {
            this.drawingPanel.setShapes(shapes);
        }
        this.setModified(true);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isModified() {
        if (this.isModified) return true;
        if (this.drawingPanel != null) {
            return this.drawingPanel.isUpdated();
        }
        return false;
    }
    
    public void setModified(boolean isModified) {
        this.isModified = isModified;
        if (this.drawingPanel != null) {
            this.drawingPanel.setBUpdated(isModified);
        }
    }
    
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        if (this.drawingPanel != null) {
            this.drawingPanel.setBackground(backgroundColor);
        }
        this.setModified(true);
    }
    
    public void addShape(GShape shape) {
        this.getShapes().add(shape);
        this.setModified(true);
    }
    
    public void removeShape(GShape shape) {
        this.getShapes().remove(shape);
        this.setModified(true);
    }
    
    public void clearShapes() {
        this.getShapes().clear();
        this.setModified(true);
    }
    
    public int getShapeCount() {
        return this.getShapes().size();
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (this.drawingPanel != null) {
            this.shapes = this.drawingPanel.getShape();
        }
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) 
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.createDrawingPanel();
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GSlide slide = (GSlide) obj;
        return name.equals(slide.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}