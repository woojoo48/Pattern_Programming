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
import global.GConstants.EFileMenuItem;
import shapes.GShape;

public class GFileMenu extends JMenu{
	private static final long serialVersionUID = 1L;

	private GDrawingPanel drawingPanel;
	private File currentFile; 
	
	public GFileMenu() {
		super("File");
		this.currentFile = null;
		
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
		int saveOption = JOptionPane.showConfirmDialog(this, 
				 "Do you want to save the file before opening it?",
				 "save",
				 JOptionPane.YES_NO_OPTION);
		 if(saveOption == JOptionPane.YES_OPTION) {
			 save();
		} 
			JFileChooser fileChooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Shape Files (*.shapes)", "shapes");
		    fileChooser.setFileFilter(filter);

		    int result = fileChooser.showOpenDialog(this);
		    if (result == JFileChooser.APPROVE_OPTION) {
		    	 
		        File selectedFile = fileChooser.getSelectedFile();
		        
		        try {
		            FileInputStream fileInputStream = new FileInputStream(selectedFile);
		            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		            ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);

		            @SuppressWarnings("unchecked")
					Vector<GShape> loadedShapes = (Vector<GShape>) objectInputStream.readObject();
		            objectInputStream.close();

		            this.drawingPanel.setShapes(loadedShapes);
		            this.drawingPanel.repaint();
		            
		            this.currentFile = selectedFile;
		            
		            System.out.println("open : " + selectedFile.getAbsolutePath());
		            
		        } catch (IOException | ClassNotFoundException e) {
		            System.out.println("Not Open" + e.getMessage());
		            e.printStackTrace();
		        } 
		    } else {
		        System.out.println("cancel");
		    }
		
	}
	
	public void save() {
		if(this.currentFile == null) {
			this.saveAs();
		} else {
			this.saveCurrentFile(this.currentFile);
		}
	}
	
	public void saveAs() {
		System.out.println("save As");
		JFileChooser fileChooser = new JFileChooser();
		//.shapes 타입 파일만 보이도록 필터링 설정
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Shape Files(*.shapes","shapes");
		fileChooser.setFileFilter(fileFilter);
		fileChooser.setSelectedFile(new File("default.shapes"));
		
		int result = fileChooser.showSaveDialog(this);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			 File selectedFile = fileChooser.getSelectedFile();
			 //기본 확장자 명시 만약 .shapes로 끝나지 않는다면 추가
			 if (!selectedFile.getName().toLowerCase().endsWith(".shapes")) {
		            selectedFile = new File(selectedFile.getAbsolutePath() + ".shapes");
		     }
			 
			 if(selectedFile.exists()) {
				 int saveAsOption = JOptionPane.showConfirmDialog(this, 
						 "File already exist. Overwrite?",
						 "Overwrite",
						 JOptionPane.YES_NO_OPTION);
				 if(saveAsOption == JOptionPane.NO_OPTION) {
					 saveAs();
					 return;
				 }
			 }
			 saveCurrentFile(selectedFile);
		} else {
			System.out.println("cancel");
		}
	
	}
	
	private void saveCurrentFile(File file) {
		Vector<GShape> shapes = this.drawingPanel.getShape();
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
			
			objectOutputStream.writeObject(shapes);
			objectOutputStream.close();
			this.currentFile = file;
			System.out.println("save");
			} catch (IOException e) {
				System.out.println("not save");
				e.printStackTrace();
			} 
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
