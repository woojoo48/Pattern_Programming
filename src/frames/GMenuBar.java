package frames;

import javax.swing.JMenuBar;

import menus.GEditMenu;
import menus.GFileMenu;
import menus.GShapeMenu;
import slideFrame.GSlideManager;

public class GMenuBar extends JMenuBar{
    private static final long serialVersionUID = 1L;
    
    //components
    private GFileMenu fileMenu;
    private GEditMenu editMenu;
    private GShapeMenu shapeMenu;
    private GSlideManager slideManager;
    
    public GMenuBar() {
        this.fileMenu = new GFileMenu();
        this.add(this.fileMenu);
        this.editMenu = new GEditMenu();
        this.add(this.editMenu);
        this.shapeMenu = new GShapeMenu();
        this.add(this.shapeMenu);
    }

    public void initialize() {
        this.fileMenu.initialize();
        this.fileMenu.associate(slideManager);
        this.editMenu.initialize();
        this.editMenu.associate(slideManager);
        this.shapeMenu.initialize();
        this.shapeMenu.associate(slideManager);
        
        connectMenusToDrawingPanels();
    }
    
    private void connectMenusToDrawingPanels() {
        if (slideManager != null) {
            GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
            if (currentPanel != null) {
                currentPanel.setEditMenu(editMenu);
                currentPanel.setShapeMenu(shapeMenu);
            }
            
            for (int i = 0; i < slideManager.getSlideCount(); i++) {
                GDrawingPanel panel = slideManager.getSlide(i).getDrawingPanel();
                if (panel != null) {
                    panel.setEditMenu(editMenu);
                    panel.setShapeMenu(shapeMenu);
                }
            }
        }
    }

    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;	
    }
    
    public void associate(GDrawingPanel drawingPanel) {
        if (drawingPanel != null) {
            if (editMenu != null) {
                drawingPanel.setEditMenu(editMenu);
            }
            if (shapeMenu != null) {
                drawingPanel.setShapeMenu(shapeMenu);
            }
        }
    }
    
    public GEditMenu getEditMenu() {
        return this.editMenu;
    }
    
    public GShapeMenu getShapeMenu() {
        return this.shapeMenu;
    }
}