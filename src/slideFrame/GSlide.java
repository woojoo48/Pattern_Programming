// ===== GSlide.java 수정 - Custom Serialization =====

package slideFrame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import frames.GDrawingPanel;
import shapes.GShape;

public class GSlide implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // ✨ UI 컴포넌트는 transient로! (저장 안됨)
    private transient GDrawingPanel drawingPanel;
    
    // ✨ 실제 저장할 데이터들
    private String name;
    private boolean isModified;
    private Color backgroundColor;
    
    // ✨ 핵심 데이터: 도형들 (이것만 저장하면 됨!)
    private Vector<GShape> shapes;
    
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
        
        // ✨ 도형 리스트 직접 관리
        this.shapes = new Vector<GShape>();
        
        //components
        this.createDrawingPanel();
    }
    
    public GSlide(GSlide other) {
        //attributes
        this.name = other.name + " 복사본";
        this.isModified = false;
        this.backgroundColor = other.backgroundColor;
        this.thumbnailNeedsUpdate = true;
        
        // ✨ 도형들 복사
        this.shapes = new Vector<GShape>(other.shapes);
        
        //components
        this.createDrawingPanel();
    }

    private void createDrawingPanel() {
        this.drawingPanel = new GDrawingPanel();
        this.drawingPanel.initialize();
        this.drawingPanel.setBackground(this.backgroundColor);
        
        // ✨ 기존 도형들이 있다면 DrawingPanel에 설정
        if (this.shapes != null) {
            this.drawingPanel.setShapes(this.shapes);
        }
    }
    
    // ===== 핵심 접근자 메서드 =====
    
    public GDrawingPanel getDrawingPanel() {
        if (this.drawingPanel == null) {
            this.createDrawingPanel();
        }
        return this.drawingPanel;
    }
    
    // ✨ 도형 관리 - shapes 필드를 직접 사용하되, DrawingPanel과 동기화
    public Vector<GShape> getShapes() {
        // DrawingPanel이 있으면 그쪽에서 가져오기 (최신 상태)
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
        this.thumbnailNeedsUpdate = true;
    }
    
    // ===== 기본 속성 관리 =====
    
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
    
    // ===== 도형 관리 메서드들 (기존과 동일) =====
    
    public void addShape(GShape shape) {
        this.getShapes().add(shape);  // getShapes()가 동기화 처리
        this.setModified(true);
    }
    
    public void removeShape(GShape shape) {
        this.getShapes().remove(shape);
        this.setModified(true);
    }
    
    public void removeShape(int index) {
        Vector<GShape> shapes = this.getShapes();
        if (index >= 0 && index < shapes.size()) {
            shapes.remove(index);
            this.setModified(true);
        }
    }
    
    public void clearShapes() {
        this.getShapes().clear();
        this.setModified(true);
    }
    
    public int getShapeCount() {
        return this.getShapes().size();
    }
    
    public boolean isEmpty() {
        return this.getShapes().isEmpty();
    }
    
    // ===== 썸네일 관련 메서드들 =====
    
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
            
            if (this.shapes != null && !this.shapes.isEmpty()) {
                double scaleX = (double) width / 1000.0;
                double scaleY = (double) height / 600.0;
                g2d.scale(scaleX, scaleY);
                
                for (GShape shape : this.shapes) {
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
    
    // ✨ 핵심: Custom Serialization
    // 파일에 저장할 때 - 필요한 데이터만 저장
    private void writeObject(ObjectOutputStream out) throws IOException {
        // DrawingPanel에서 최신 도형 상태 가져오기
        if (this.drawingPanel != null) {
            this.shapes = this.drawingPanel.getShape();
        }
        
        // 기본 필드들 저장 (shapes, name, isModified, backgroundColor)
        out.defaultWriteObject();
        
        System.out.println("슬라이드 '" + this.name + "' 저장됨 - 도형 " + this.shapes.size() + "개");
    }
    
    // 파일에서 로드할 때 - 저장된 데이터로 복원
    private void readObject(ObjectInputStream in) 
            throws IOException, ClassNotFoundException {
        // 기본 필드들 복원 (shapes, name, isModified, backgroundColor)
        in.defaultReadObject();
        
        // transient 필드들 초기화
        this.thumbnailNeedsUpdate = true;
        
        // DrawingPanel 재생성 및 도형들 설정
        this.createDrawingPanel();
        
        System.out.println("슬라이드 '" + this.name + "' 로드됨 - 도형 " + this.shapes.size() + "개");
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