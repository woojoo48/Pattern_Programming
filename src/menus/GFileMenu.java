package menus;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class GFileMenu extends JMenu{
	private static final long serialVersionUID = 1L;

	private JMenuItem newItem;
	
	public GFileMenu() {
		super("File");
		
		this.newItem = new JMenuItem("new");
		this.add(this.newItem);
		
	}

	public void initialize() {
		
	}

}
