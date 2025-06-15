package menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import commands.CommandManager;
import commands.CopyCommand;
import commands.DeleteCommand;
import commands.GroupCommand;
import commands.PasteCommand;
import commands.UngroupCommand;
import frames.GDrawingPanel;
import global.GConstants;
import global.GConstants.EEditMenuItem;
import shapes.GShape;
import slideFrame.GSlideManager;

public class GEditMenu extends JMenu {
    private static final long serialVersionUID = 1L;

    private GSlideManager slideManager;
    private CommandManager commandManager;
    private Vector<GShape> clipboard;
    private int nextGroupId = 1;
    
    public GEditMenu() {
        super(GConstants.getEditMenuLabel());
        
        this.commandManager = new CommandManager();
        this.clipboard = new Vector<GShape>();
        
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
    
    public CommandManager getCommandManager() {
        return this.commandManager;
    }
    
    private GDrawingPanel getCurrentPanel() {
        return slideManager != null ? slideManager.getCurrentDrawingPanel() : null;
    }
    
    private Vector<GShape> getSelectedShapes() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return new Vector<>();
        
        Vector<GShape> selected = new Vector<>();
        for (GShape shape : currentPanel.getShape()) {
            if (shape.isSelected()) {
                selected.add(shape);
            }
        }
        return selected;
    }
    
    private void clearAllSelection() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        for (GShape shape : currentPanel.getShape()) {
            shape.setSelected(false);
        }
        currentPanel.setSelectedShape(null);
    }
    
    private void updatePanelAndRepaint() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel != null) {
            currentPanel.setBUpdated(true);
            currentPanel.repaint();
        }
    }
    
    // Edit operations
    public void undo() {
        if (commandManager.undo()) {
            clearAllSelection();
            updatePanelAndRepaint();
        }
    }
    
    public void redo() {
        if (commandManager.redo()) {
            clearAllSelection();
            updatePanelAndRepaint();
        }
    }
    
    public void copySelectedShapes() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.isEmpty()) {
            return;
        }
        
        CopyCommand copyCommand = new CopyCommand(clipboard, selected);
        copyCommand.execute();
    }
    
    public void pasteShapes() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null || clipboard.isEmpty()) {
            return;
        }
        
        PasteCommand pasteCommand = new PasteCommand(currentPanel.getShape(), clipboard);
        commandManager.executeCommand(pasteCommand);
        
        clearAllSelection();
        updatePanelAndRepaint();
    }
    
    public void deleteSelectedShapes() {
        GDrawingPanel currentPanel = getCurrentPanel();
        Vector<GShape> selected = getSelectedShapes();
        if (currentPanel == null || selected.isEmpty()) {
            return;
        }
        
        DeleteCommand deleteCommand = new DeleteCommand(currentPanel.getShape(), selected);
        commandManager.executeCommand(deleteCommand);
        
        clearAllSelection();
        updatePanelAndRepaint();
    }
    
    public void group() {
        GDrawingPanel currentPanel = getCurrentPanel();
        Vector<GShape> selected = getSelectedShapes();
        if (currentPanel == null || selected.size() < 2) {
            return;
        }
        
        GroupCommand groupCommand = new GroupCommand(currentPanel.getShape(), selected, nextGroupId++);
        commandManager.executeCommand(groupCommand);
        updatePanelAndRepaint();
    }
    
    public void ungroup() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        GShape selectedShape = currentPanel.getSelectedShape();
        if (selectedShape == null || !selectedShape.isGrouped()) {
            return;
        }
        
        UngroupCommand ungroupCommand = new UngroupCommand(currentPanel.getShape(), selectedShape.getGroupId());
        commandManager.executeCommand(ungroupCommand);
        updatePanelAndRepaint();
    }
    
    public void bringToFront() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        GShape selectedShape = currentPanel.getSelectedShape();
        if (selectedShape == null) return;
        
        Vector<GShape> shapes = currentPanel.getShape();
        
        if (selectedShape.isGrouped()) {
            moveGroupToPosition(shapes, selectedShape.getGroupId(), shapes.size());
        } else {
            boolean removed = shapes.remove(selectedShape);
            if (removed) {
                shapes.add(selectedShape);
            }
        }
        
        updatePanelAndRepaint();
    }
    
    public void sendToBack() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        GShape selectedShape = currentPanel.getSelectedShape();
        if (selectedShape == null) return;
        
        Vector<GShape> shapes = currentPanel.getShape();
        
        if (selectedShape.isGrouped()) {
            moveGroupToPosition(shapes, selectedShape.getGroupId(), 0);
        } else {
            boolean removed = shapes.remove(selectedShape);
            if (removed) {
                shapes.add(0, selectedShape);
            }
        }
        
        updatePanelAndRepaint();
    }
    
    public void bringForward() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        GShape selectedShape = currentPanel.getSelectedShape();
        if (selectedShape == null) return;
        
        Vector<GShape> shapes = currentPanel.getShape();
        int currentIndex = shapes.indexOf(selectedShape);
        if (currentIndex == -1 || currentIndex >= shapes.size() - 1) return;
        
        shapes.remove(currentIndex);
        shapes.add(currentIndex + 1, selectedShape);
        updatePanelAndRepaint();
    }
    
    public void sendBackward() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        GShape selectedShape = currentPanel.getSelectedShape();
        if (selectedShape == null) return;
        
        Vector<GShape> shapes = currentPanel.getShape();
        int currentIndex = shapes.indexOf(selectedShape);
        if (currentIndex == -1 || currentIndex <= 0) return;
        
        shapes.remove(currentIndex);
        shapes.add(currentIndex - 1, selectedShape);
        updatePanelAndRepaint();
    }
    
    private void moveGroupToPosition(Vector<GShape> shapes, int groupId, int position) {
        Vector<GShape> groupShapes = new Vector<>();
        
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).getGroupId() == groupId) {
                groupShapes.add(0, shapes.remove(i));
            }
        }
        
        for (int i = 0; i < groupShapes.size(); i++) {
            shapes.add(Math.min(position + i, shapes.size()), groupShapes.get(i));
        }
    }
    
    // Validation methods
    public boolean canUndo() {
        return commandManager.canUndo();
    }
    
    public boolean canRedo() {
        return commandManager.canRedo();
    }
    
    public boolean canGroup() {
        return getSelectedShapes().size() >= 2;
    }
    
    public boolean canUngroup() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return false;
        
        GShape selectedShape = currentPanel.getSelectedShape();
        return selectedShape != null && selectedShape.isGrouped();
    }
    
    public boolean canDelete() {
        return !getSelectedShapes().isEmpty();
    }
    
    public boolean canCopy() {
        return !getSelectedShapes().isEmpty();
    }
    
    public boolean canPaste() {
        return !clipboard.isEmpty();
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