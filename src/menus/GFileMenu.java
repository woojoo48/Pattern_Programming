package menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import frames.GDrawingPanel;
import global.GConstants.EFileMenuItem;
import shapes.GShape;

public class GFileMenu extends JMenu{
	private static final long serialVersionUID = 1L;

	private GDrawingPanel drawingPanel;
	
	public GFileMenu() {
		super("File");
		
		ActionHandler actionHandler = new ActionHandler();
		for(EFileMenuItem eMenuItem : EFileMenuItem.values()) {
			JMenuItem menuItem = new JMenuItem(eMenuItem.getName());
			menuItem.addActionListener(actionHandler);
			menuItem.setActionCommand(eMenuItem.name());
			this.add(menuItem);
		}
	}

	public void initialize() {
		
	}
	
	public void associate(GDrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;
	}

	//fileMunu method
	public void newPanel() {
		System.out.println("newPanel");
	}
	
	public void open() {
		System.out.println("open");

	}
	
	public void save() {
		System.out.println("save");

		Vector<GShape> shapes = this.drawingPanel.getShape();
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("file");
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
			
			objectOutputStream.writeObject(shapes);
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveAs() {
		System.out.println("saveAs");
	}
	
	public void print() {
		System.out.println("print");

	}
	
	public void quit() {
		System.out.println("quit");

	}
	
	private void invokeMethod(String methodName) {
		try {
			//객체의 메모리를 만들어서 메모리 주소를 호출하는 것임. 만들어진 메모리 주소를 던져주는것. invoke(this)부분 얘기임
			this.getClass().getMethod(methodName).invoke(this);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException exception) {
			exception.printStackTrace();
		} 
	}
	private class ActionHandler implements ActionListener{

		//구현 필요
		@Override
		public void actionPerformed(ActionEvent event) {
			EFileMenuItem eFileMenuItem = EFileMenuItem.valueOf(event.getActionCommand());
			invokeMethod(eFileMenuItem.getMethodName());
		}
	}

}
