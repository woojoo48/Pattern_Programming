package slideFrame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import frames.GDrawingPanel;
import frames.GShapeToolBar.EShapeTool;
import global.GConstants;
import shapes.GShape;

public class GSlideManager extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Vector<GSlide> slides;          
    private GSlideThumbnailPanel thumbnailPanel;  
    private JPanel slideContainer;                  
    private JSplitPane splitPane;                     
    private CardLayout cardLayout;                    
    
    private int currentSlideIndex;
    private boolean isModified;
    private EShapeTool currentTool;
    
    public GSlideManager() {
        this.currentSlideIndex = -1;
        this.isModified = false;
        this.currentTool = null;
        
        this.slides = new Vector<GSlide>();
        this.slideContainer = createSlideContainer();
        this.cardLayout = new CardLayout();
        this.slideContainer.setLayout(cardLayout);
        
        this.thumbnailPanel = new GSlideThumbnailPanel();
        
        this.setLayout(new BorderLayout());
        
        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitPane.setLeftComponent(thumbnailPanel);      
        this.splitPane.setRightComponent(slideContainer);     
        this.splitPane.setDividerLocation(GConstants.getPanelWidth());
        this.splitPane.setResizeWeight(0.0);
        this.splitPane.setOneTouchExpandable(true);
        
        this.add(splitPane, BorderLayout.CENTER);
        
        String defaultSlideName = GConstants.getSlideName("defaultSlideName") + " 1";
        this.addSlide(defaultSlideName);
    }
    
    public void initialize() {
        this.thumbnailPanel.associate(this);
        this.thumbnailPanel.initialize();
        
        for (GSlide slide : slides) {
            if (slide.getDrawingPanel() != null) {
                slide.getDrawingPanel().initialize();
            }
        }
        
        if (!slides.isEmpty()) {
            this.setCurrentSlideIndex(0);
        }
        
        this.setModified(false);
    }

    private JPanel createSlideContainer() {
        JPanel container = new JPanel();
        container.setBackground(GConstants.getBackgroundColor());
        return container;
    }
    
    public void setCurrentTool(EShapeTool tool) {
        this.currentTool = tool;
        
        for (GSlide slide : slides) {
            if (slide.getDrawingPanel() != null) {
                slide.getDrawingPanel().setEShapeTool(tool);
            }
        }
    }
    
    public EShapeTool getCurrentTool() {
        return this.currentTool;
    }
    
    public GSlide addSlide() {
        String slideNameFormat = GConstants.getSlideName("slideNumberFormat");
        String slideName = slideNameFormat.replace("{0}", String.valueOf(slides.size() + 1));
        return addSlide(slideName);
    }
    
    public GSlide addSlide(String name) {
        GSlide newSlide = new GSlide(name);
        
        if (newSlide.getDrawingPanel() != null && this.currentTool != null) {
            newSlide.getDrawingPanel().setEShapeTool(this.currentTool);
        }
        
        this.slides.add(newSlide);
        
        int slideIndex = slides.size() - 1;
        slideContainer.add(newSlide.getDrawingPanel(), "slide" + slideIndex);
        
        this.setCurrentSlideIndex(slideIndex);
        this.setModified(true);
        return newSlide;
    }
    
    public boolean removeSlide(int index) {
        if (index < 0 || index >= slides.size() || slides.size() <= 1) {
            return false;
        }
        
        this.slides.remove(index);
        
        if (currentSlideIndex >= slides.size()) {
            this.setCurrentSlideIndex(slides.size() - 1);
        } else if (currentSlideIndex > index) {
            this.setCurrentSlideIndex(currentSlideIndex - 1);
        }
        
        this.refreshCardLayout();
        this.setModified(true);
        return true;
    }
    
    public boolean removeCurrentSlide() {
        return removeSlide(currentSlideIndex);
    }

    public void switchToSlide(int index) {
        this.setCurrentSlideIndex(index);
        
        GDrawingPanel currentPanel = getCurrentDrawingPanel();
        if (currentPanel != null && this.currentTool != null) {
            currentPanel.setEShapeTool(this.currentTool);
        }
    }

    public GSlide previousSlide() {
        if (hasPreviousSlide()) {
            setCurrentSlideIndex(currentSlideIndex - 1);
            return getCurrentSlide();
        }
        return null;
    }
    
    public GSlide nextSlide() {
        if (hasNextSlide()) {
            setCurrentSlideIndex(currentSlideIndex + 1);
            return getCurrentSlide();
        }
        return null;
    }
    
    private void refreshCardLayout() {
        slideContainer.removeAll();
        
        for (int i = 0; i < slides.size(); i++) {
            GSlide slide = slides.get(i);
            slideContainer.add(slide.getDrawingPanel(), "slide" + i);
        }
        
        if (currentSlideIndex >= 0) {
            cardLayout.show(slideContainer, "slide" + currentSlideIndex);
        }
        
        slideContainer.revalidate();
        slideContainer.repaint();
    }

    public Vector<Vector<GShape>> getAllSlideShapes() {
        Vector<Vector<GShape>> allShapes = new Vector<>();
        for (GSlide slide : slides) {
            allShapes.add(slide.getShapes());
        }
        return allShapes;
    }
    
    public void loadAllSlides(Vector<GSlide> loadedSlides) {
        this.slides.clear();
        slideContainer.removeAll();
        
        this.slides.addAll(loadedSlides);
        
        for (int i = 0; i < slides.size(); i++) {
            GSlide slide = slides.get(i);
            if (slide.getDrawingPanel() != null && this.currentTool != null) {
                slide.getDrawingPanel().setEShapeTool(this.currentTool);
            }
            slideContainer.add(slide.getDrawingPanel(), "slide" + i);
        }
        
        if (!slides.isEmpty()) {
            this.setCurrentSlideIndex(0);
        }
        
        this.refreshCardLayout();
        this.setModified(false);
    }
    
    public void newPresentation() {
        this.slides.clear();
        slideContainer.removeAll();
        
        String defaultSlideName = GConstants.getSlideName("defaultSlideName") + " 1";
        this.addSlide(defaultSlideName);
        this.setModified(false);
    }
    
    public GSlide getCurrentSlide() {
        if (currentSlideIndex >= 0 && currentSlideIndex < slides.size()) {
            return slides.get(currentSlideIndex);
        }
        return null;
    }
    
    public GDrawingPanel getCurrentDrawingPanel() {
        GSlide currentSlide = getCurrentSlide();
        if (currentSlide != null) {
            return currentSlide.getDrawingPanel();
        }
        return null;
    }
    
    public int getCurrentSlideIndex() {
        return this.currentSlideIndex;
    }
    
    public void setCurrentSlideIndex(int index) {
        if (index >= 0 && index < slides.size()) {
            this.currentSlideIndex = index;
            cardLayout.show(slideContainer, "slide" + index);
        }
    }
    
    public int getSlideCount() {
        return this.slides.size();
    }
    
    public GSlide getSlide(int index) {
        if (index >= 0 && index < slides.size()) {
            return slides.get(index);
        }
        return null;
    }
    
    public Vector<GSlide> getAllSlides() {
        return new Vector<GSlide>(this.slides);
    }
    
    public boolean hasNextSlide() {
        return currentSlideIndex < slides.size() - 1;
    }
    
    public boolean hasPreviousSlide() {
        return currentSlideIndex > 0;
    }
    
    public boolean isModified() {
        if (this.isModified) return true;
        
        for (GSlide slide : slides) {
            if (slide.isModified()) {
                return true;
            }
        }
        return false;
    }
    
    public void setModified(boolean isModified) {
        this.isModified = isModified;
        if (!isModified) {
            for (GSlide slide : slides) {
                slide.setModified(false);
            }
        }
    }

    public GSlideThumbnailPanel getThumbnailPanel() {
        return this.thumbnailPanel;
    }
}