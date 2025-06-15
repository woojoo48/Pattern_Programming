package menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import frames.GDrawingPanel;
import global.GConstants.EEditMenuItem;
import slideFrame.GSlideManager;

public class GEditMenu extends JMenu {
    private static final long serialVersionUID = 1L;

    private GSlideManager slideManager;
    
    public GEditMenu() {
        super("Edit");
        
        ActionHandler actionHandler = new ActionHandler();
        
        for(EEditMenuItem eMenuItem : EEditMenuItem.values()) {
            if (eMenuItem == EEditMenuItem.eCopy || 
                eMenuItem == EEditMenuItem.eGroup || 
                eMenuItem == EEditMenuItem.eBringToFront) {
                this.add(new JSeparator());
            }
            
            JMenuItem menuItem = new JMenuItem(eMenuItem.getName());
            menuItem.addActionListener(actionHandler);
            menuItem.setActionCommand(eMenuItem.name());
            this.add(menuItem);
        }
    }

    public void initialize() {
    }
    
    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;
    }
    
    public void undo() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.undo();
        }
    }
    
    public void redo() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.redo();
        }
    }
    
    public void copySelectedShapes() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.copySelectedShapes();
        }
    }
    
    public void pasteShapes() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.pasteShapes();
        }
    }
    
    public void deleteSelectedShapes() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.deleteSelectedShapes();
        }
    }
    
    public void group() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.groupSelectedShapes();
        }
    }
    
    public void ungroup() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.ungroupSelectedShape();
        }
    }
    
    public void bringToFront() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.bringToFront();
        }
    }
    
    public void sendToBack() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.sendToBack();
        }
    }
    
    public void bringForward() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.bringForward();
        }
    }
    
    public void sendBackward() {
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.sendBackward();
        }
    }
    
    private void invokeMethod(String methodName) {
        try {
            this.getClass().getMethod(methodName).invoke(this);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        } 
    }
    
    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            EEditMenuItem eEditMenuItem = EEditMenuItem.valueOf(event.getActionCommand());
            invokeMethod(eEditMenuItem.getMethodName());
        }
    }
}