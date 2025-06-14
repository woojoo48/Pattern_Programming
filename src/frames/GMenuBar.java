package frames;

import javax.swing.JMenuBar;

import menus.GEditMenu;
import menus.GFileMenu;
import slideFrame.GSlideManager;

public class GMenuBar extends JMenuBar{
    private static final long serialVersionUID = 1L;
    
    //components
    private GFileMenu fileMenu;
    private GEditMenu editMenu;
    private GSlideManager slideManager;
    
    public GMenuBar() {
        this.fileMenu = new GFileMenu();
        this.add(this.fileMenu);
        this.editMenu = new GEditMenu();
        this.add(this.editMenu);
    }

    public void initialize() {
        this.fileMenu.initialize();
        this.fileMenu.associate(slideManager);
        this.editMenu.initialize();
        this.editMenu.associate(slideManager);
    }

    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;	
    }
    
    public void associate(GDrawingPanel drawingPanel) {
    }
}