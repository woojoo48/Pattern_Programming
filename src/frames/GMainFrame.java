package frames;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JFrame;

import global.GConstants;

public class GMainFrame extends JFrame{
	//attributes
	private static final long serialVersionUID = 1L;
	
	//components
	private GMenuBar menuBar;
	private GShapeToolBar toolBar;
	private GDrawingPanel drawingPanel;
	
	//association
	
	public GMainFrame() {
		//attributes
		this.setLocationRelativeTo(null);
		this.setSize(GConstants.GMainFrame.SCREEN_WIDTH, GConstants.GMainFrame.SCREEN_HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//components
		LayoutManager layout = new BorderLayout();
		this.setLayout(layout); 
		this.menuBar = new GMenuBar();
		this.setJMenuBar(menuBar);
		
		this.drawingPanel = new GDrawingPanel();
		this.toolBar = new GShapeToolBar(drawingPanel);
		this.add(drawingPanel, BorderLayout.CENTER);
		this.add(toolBar, BorderLayout.NORTH);
		
		
	}

	public void initialize() {
		// associate
		this.menuBar.associate(this.drawingPanel);
		this.toolBar.associate(this.drawingPanel);
		//associated attributes
		this.setVisible(true);
		
		this.menuBar.initialize();
		this.toolBar.initialize();
		this.drawingPanel.initialize();
	}
}
