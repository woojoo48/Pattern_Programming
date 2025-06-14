package slideFrame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import frames.GDrawingPanel;
import global.GConstants;

public class GSlideManager extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //components
    private Vector<GSlide> slides;          
    private GSlideThumbnailPanel thumbnailPanel;  
    private JPanel slideContainer;                  
    private JSplitPane splitPane;                     
    private CardLayout cardLayout;                    
    
    //attributes
    private int currentSlideIndex;
    private boolean isModified;
    

    
    public GSlideManager() {

        this.currentSlideIndex = -1;
        this.isModified = false;
        
        this.slides = new Vector<GSlide>();
        this.slideContainer = createSlideContainer();
        this.cardLayout = new CardLayout();
        this.slideContainer.setLayout(cardLayout);
        
        this.thumbnailPanel = new GSlideThumbnailPanel();
        
        this.setLayout(new BorderLayout());
        
        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitPane.setLeftComponent(thumbnailPanel);      
        this.splitPane.setRightComponent(slideContainer);     
        this.splitPane.setDividerLocation(GConstants.GSlidePanel.PANEL_WIDTH);
        this.splitPane.setResizeWeight(0.0);
        this.splitPane.setOneTouchExpandable(true);
        
        this.add(splitPane, BorderLayout.CENTER);
        
        this.addSlide("슬라이드 1");
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
        container.setBackground(Color.WHITE);
        return container;
    }
    
    public GSlide addSlide() {
        return addSlide("슬라이드 " + (slides.size() + 1));
    }
    

    public GSlide addSlide(String name) {
        GSlide newSlide = new GSlide(name);
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