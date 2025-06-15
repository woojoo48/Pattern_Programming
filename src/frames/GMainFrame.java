package frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;

import javax.swing.JFrame;

import global.GConstants;
import slideFrame.GSlideManager;

public class GMainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    //components
    private GMenuBar menuBar;
    private GShapeToolBar toolBar;
    private GSlideManager slideManager;
    
    public GMainFrame() {
        GConstants.getInstance();
        this.setSize(GConstants.getScreenWidth(), GConstants.getScreenHeight());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);        
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        
        int x = GConstants.getScreenX();
        int y = GConstants.getScreenY();
        
        if (x == 0 && y == 0) {
            x = (screenSize.width - windowSize.width) / 2;
            y = (screenSize.height - windowSize.height) / 2;
            x = Math.max(0, x);
            y = Math.max(0, y);
        }
        
        this.setLocation(x, y);
        
        LayoutManager layout = new BorderLayout();
        this.setLayout(layout);
        
        this.menuBar = new GMenuBar();
        this.setJMenuBar(menuBar);
        
        this.slideManager = new GSlideManager();
        this.toolBar = new GShapeToolBar(null);
        
        this.add(toolBar, BorderLayout.NORTH);
        this.add(slideManager, BorderLayout.CENTER);
    }

    public void initialize() {
        this.menuBar.associate(this.slideManager);
        this.toolBar.associate(this.slideManager);
        
        this.setVisible(true);
        
        this.menuBar.initialize();
        this.toolBar.initialize();
        this.slideManager.initialize();
    }
    
    public GSlideManager getSlideManager() {
        return this.slideManager;
    }
    
    public GDrawingPanel getCurrentDrawingPanel() {
        return this.slideManager.getCurrentDrawingPanel();
    }
}