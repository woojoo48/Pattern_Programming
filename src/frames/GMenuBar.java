package frames;

import javax.swing.JMenuBar;

import menus.GFileMenu;

public class GMenuBar extends JMenuBar{
	private static final long serialVersionUID = 1L;
	//components
	private GFileMenu fileMenu;
	//association
	private GDrawingPanel drawingPanel;
	
	public GMenuBar() {
		this.fileMenu = new GFileMenu();
		this.add(this.fileMenu);
	}


	public void initialize() {
		this.fileMenu.initialize();
		this.fileMenu.associate(drawingPanel);
	}


	public void associate(GDrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;	
	}

}
