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
import slideFrame.GSlide;
import slideFrame.GSlideManager;

public class GFileMenu extends JMenu{
    private static final long serialVersionUID = 1L;

    private GSlideManager slideManager;
    private File dir;
    private File file;
    private JFileChooser fileChooser;
    
    public GFileMenu() {
        super(GConstants.getFileMenuLabel());
        
        ActionHandler actionHandler = new ActionHandler();
        for(EFileMenuItem eMenuItem : EFileMenuItem.values()) {
            JMenuItem menuItem = new JMenuItem(eMenuItem.getName());
            menuItem.addActionListener(actionHandler);
            menuItem.setActionCommand(eMenuItem.name());
            this.add(menuItem);
        }
    }

    public void initialize() {
        this.dir = new File(GConstants.getDefaultFilePath());
        this.file = null;
        
        this.fileChooser = new JFileChooser(this.dir);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                GConstants.getExtensionFilter(), 
                GConstants.getDefaultExtension());
        this.fileChooser.setFileFilter(filter);
        this.fileChooser.setSelectedFile(new File(GConstants.getDefaultFileName()));
    }
    
    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;
    }
    
    public void associate(GDrawingPanel drawingPanel) {
    }

    public void newPanel() {
        System.out.println(GConstants.getFileMessage("newPanelMsg"));
        if(this.close()) {
            this.slideManager.newPresentation();
            this.file = null;
        }
    }
    
    public void open() {
        if(this.close()) {
            int result = fileChooser.showOpenDialog(this.slideManager);
            
            if(result == JFileChooser.APPROVE_OPTION) {
                this.loadFileChooser();
                
                try {
                    FileInputStream fileInputStream = new FileInputStream(this.file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);

                    @SuppressWarnings("unchecked")
                    Vector<GSlide> loadedSlides = (Vector<GSlide>) objectInputStream.readObject();
                    objectInputStream.close();
                        
                    this.slideManager.loadAllSlides(loadedSlides);
                                
                } catch (IOException | ClassNotFoundException e) {
                    String errorMsg = GConstants.getFileMessage("openFailMsg");
                    System.out.println(errorMsg.replace("{0}", e.getMessage()));
                    e.printStackTrace();
                }
            } else {
                System.out.println(GConstants.getFileMessage("openCancelMsg"));
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
        System.out.println(GConstants.getFileMenuLabel("eSaveAs"));
        
        boolean bCancel = false;

        int result = this.fileChooser.showSaveDialog(this.slideManager);
        
        if(result == JFileChooser.APPROVE_OPTION) {
            this.loadFileChooser();
            
            String extension = "." + GConstants.getDefaultExtension();
            if (!file.getName().toLowerCase().endsWith(extension)) {
                file = new File(file.getAbsolutePath() + extension);
            }
            bCancel = this.saveToFile();
        } else {
            bCancel = true;
            System.out.println(GConstants.getFileMessage("saveCancelMsg"));
        }
        return bCancel;
    }
    
    private boolean saveToFile() {
        try {
            System.out.println(GConstants.getFileMessage("saveMsg"));
            
            Vector<GSlide> allSlides = this.slideManager.getAllSlides();
            
            FileOutputStream fileOutputStream = new FileOutputStream(this.file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            
            objectOutputStream.writeObject(allSlides);
            objectOutputStream.close();
            
            this.slideManager.setModified(false);
            return false;
        } catch (IOException e) {
            System.out.println(GConstants.getFileMessage("saveFailMsg"));
            e.printStackTrace();
            return true;
        }
    }
    
    public void quit() {
        System.out.println(GConstants.getFileMessage("quitMsg"));
        if(this.close()) {
            System.exit(0);
        }
    }
    
    public boolean close() {
        boolean bCancel = false;
        
        if(this.slideManager.isModified()) {
            int reply = JOptionPane.showConfirmDialog(
                this.slideManager, 
                GConstants.getFileMessage("saveConfirmMsg")
            );
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
    
    private void invokeMethod(String methodName) {
        try {
            this.getClass().getMethod(methodName).invoke(this);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        } 
    }
    
    private class ActionHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event) {
            EFileMenuItem eFileMenuItem = EFileMenuItem.valueOf(event.getActionCommand());
            invokeMethod(eFileMenuItem.getMethodName());
        }
    }
}