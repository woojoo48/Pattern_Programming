package slideFrame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;

import frames.GDrawingPanel;
import shapes.GShape;

public class GSlide implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //components
    private GDrawingPanel drawingPanel;
    
    //attributes
    private String name;
    private boolean isModified;
    private Color backgroundColor;
    
    // 썸네일 관련 (transient - 런타임에만 사용)
    private transient BufferedImage thumbnail;
    private transient boolean thumbnailNeedsUpdate;
    
    public GSlide() {
        this("새 슬라이드");
    }
    

    public GSlide(String name) {
        //attributes
        this.name = name;
        this.isModified = false;
        this.backgroundColor = Color.WHITE;
        this.thumbnailNeedsUpdate = true;
        
        //components
        this.createDrawingPanel();
    }
    

    public GSlide(GSlide other) {
        //attributes
        this.name = other.name + " 복사본";
        this.isModified = false;
        this.backgroundColor = other.backgroundColor;
        this.thumbnailNeedsUpdate = true;
        
        //components
        this.createDrawingPanel();
        if (other.drawingPanel != null) {
            Vector<GShape> originalShapes = other.drawingPanel.getShape();
            Vector<GShape> copiedShapes = new Vector<GShape>(originalShapes);
            this.drawingPanel.setShapes(copiedShapes);
        }
    }
    

    private void createDrawingPanel() {
        this.drawingPanel = new GDrawingPanel();
        this.drawingPanel.initialize();
        this.drawingPanel.setBackground(this.backgroundColor);
    }
    

    public GDrawingPanel getDrawingPanel() {
        if (this.drawingPanel == null) {
            this.createDrawingPanel();
        }
        return this.drawingPanel;
    }
    

    public Vector<GShape> getShapes() {
        return this.getDrawingPanel().getShape();
    }

    public void setShapes(Vector<GShape> shapes) {
        this.getDrawingPanel().setShapes(shapes);
        this.setModified(true);
        this.thumbnailNeedsUpdate = true;
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
        if (isModified) {
            this.thumbnailNeedsUpdate = true;
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
        this.getDrawingPanel().getShape().add(shape);
        this.setModified(true);
    }
    
    public void removeShape(GShape shape) {
        this.getDrawingPanel().getShape().remove(shape);
        this.setModified(true);
    }
    
    public void removeShape(int index) {
        Vector<GShape> shapes = this.getDrawingPanel().getShape();
        if (index >= 0 && index < shapes.size()) {
            shapes.remove(index);
            this.setModified(true);
        }
    }
    
    public void clearShapes() {
        this.getDrawingPanel().getShape().clear();
        this.setModified(true);
    }
    
    public int getShapeCount() {
        return this.getDrawingPanel().getShape().size();
    }
    
    public boolean isEmpty() {
        return this.getDrawingPanel().getShape().isEmpty();
    }
    
    
    public BufferedImage getThumbnail() {
        return this.thumbnail;
    }
    
    public void setThumbnail(BufferedImage thumbnail) {
        this.thumbnail = thumbnail;
        this.thumbnailNeedsUpdate = false;
    }
    
    public boolean isThumbnailNeedsUpdate() {
        return this.thumbnailNeedsUpdate;
    }
    
    public void markThumbnailForUpdate() {
        this.thumbnailNeedsUpdate = true;
    }

    public BufferedImage generateThumbnail(int width, int height) {
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(this.backgroundColor);
            g2d.fillRect(0, 0, width, height);
            
            if (this.drawingPanel != null) {
                double scaleX = (double) width / 1000.0;
                double scaleY = (double) height / 600.0;
                g2d.scale(scaleX, scaleY);
                
                Vector<GShape> shapes = this.drawingPanel.getShape();
                for (GShape shape : shapes) {
                    shape.draw(g2d);
                }
            }
        } finally {
            g2d.dispose();
        }
        
        this.thumbnail = thumbnail;
        this.thumbnailNeedsUpdate = false;
        return thumbnail;
    }

    private void readObject(ObjectInputStream in) 
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        this.createDrawingPanel();
        this.thumbnailNeedsUpdate = true;
    }
    
    @Override
    public String toString() {
        return this.name + " (" + this.getShapeCount() + "개 도형)";
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