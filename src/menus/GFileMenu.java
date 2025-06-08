package menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import frames.GDrawingPanel;
import global.GConstants;
import global.GConstants.EFileMenuItem;
import shapes.GShape;

public class GFileMenu extends JMenu{
	private static final long serialVersionUID = 1L;

	private GDrawingPanel drawingPanel;
	private File dir;
	private File file;
	private JFileChooser fileChooser;
	
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
		this.dir = new File(GConstants.GFileMenu.DEFAULT_FILE_ROOT);
		this.file = null;
		
		this.fileChooser = new JFileChooser(this.dir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				GConstants.GFileMenu.DEFAULT_FILE_EXTENSTION, 
				GConstants.GFileMenu.DEFAULT_FILE_EXTENSTION_TYPE);
		this.fileChooser.setFileFilter(filter);
		this.fileChooser.setSelectedFile(new File(GConstants.GFileMenu.DEFAULT_FILE_NAME));
	}
	
	public void associate(GDrawingPanel drawingPanel) {
		this.drawingPanel = drawingPanel;
	}

	//fileMunu method
	public void newPanel() {
		System.out.println("newPanel");
		if(this.close()) {
			this.drawingPanel.initialize();
			this.file = null;
		}
	}
	
	public void open() {
		if(this.close()) {
			int result = fileChooser.showOpenDialog(this.drawingPanel);
	        
	        if(result == JFileChooser.APPROVE_OPTION) {
	            this.loadFileChooser();
	            
	            try {
	                FileInputStream fileInputStream = new FileInputStream(this.file);
	                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
	                ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);

	                @SuppressWarnings("unchecked")
	                Vector<GShape> loadedShapes = (Vector<GShape>) objectInputStream.readObject();
	                objectInputStream.close();
	                    
	                this.drawingPanel.setShapes(loadedShapes);
	                this.drawingPanel.repaint();
	                            
	            } catch (IOException | ClassNotFoundException e) {
	                System.out.println(GConstants.GFileMenu.OPEN_NOT + e.getMessage());
	                e.printStackTrace();
	            }
	        } else {
	            System.out.println(GConstants.GFileMenu.CANCEL);
	        }
	    }
	}
	
	public boolean save() {
		if(this.file == null) {
			return this.saveAs();
			} else {
			return this.saveToFile();
			}
		}
	
	public boolean saveAs() {
		System.out.println(GConstants.GFileMenu.SAVE_AS);
		
		boolean bCancel = false;

		int result = this.fileChooser.showSaveDialog(this.drawingPanel);
		
		if(result == JFileChooser.APPROVE_OPTION) {
	        this.loadFileChooser();
	        
			 if (!file.getName().toLowerCase().endsWith(GConstants.GFileMenu.DEFAULT_FILE_TYPE)) {
		            file = new File(file.getAbsolutePath() + GConstants.GFileMenu.DEFAULT_FILE_TYPE);
		     	}
			 bCancel = this.saveToFile();
			} else {
				bCancel = true;
				System.out.println(GConstants.GFileMenu.CANCEL);
			}
			return bCancel;
	}
	
	private boolean saveToFile() {
		try {
			System.out.println(GConstants.GFileMenu.SAVE);
			Vector<GShape> shapes = this.drawingPanel.getShape();
			FileOutputStream fileOutputStream = new FileOutputStream(this.file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
			
			objectOutputStream.writeObject(shapes);
			objectOutputStream.close();
			this.drawingPanel.setBUpdated(false);
			return false;
		} catch (IOException e) {
			System.out.println(GConstants.GFileMenu.SAVE_NOT);
			e.printStackTrace();
			return true;
		}
	}
	
	public void quit() {
		System.out.println("quit");
		if(this.close()) {
			System.exit(0);
		}

	}
	
	public boolean close() {
		 boolean bCancel = false;
		 
		    if(this.drawingPanel.isUpdated()) {
		        int reply = JOptionPane.showConfirmDialog(this.drawingPanel, GConstants.GFileMenu.SAVE_OPTION_MSG);
		        if(reply == JOptionPane.CANCEL_OPTION) {
		            bCancel = true;
		        } else if(reply == JOptionPane.OK_OPTION) {
		            bCancel = this.save();
		        }
		    }
		    return !bCancel;
	}
	
	private void loadFileChooser() {
		this.dir = this.fileChooser.getCurrentDirectory();
		this.file = this.fileChooser.getSelectedFile();
	}
	
	//actionHandler
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
