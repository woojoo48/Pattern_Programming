package frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JPanel;

import commands.CommandManager;
import commands.CopyCommand;
import commands.DeleteCommand;
import commands.GroupCommand;
import commands.PasteCommand;
import commands.UngroupCommand;
import frames.GShapeToolBar.EShapeTool;
import shapes.GShape;
import shapes.GShape.EAnchor;
import shapes.GShape.EPoints;
import transformers.GDrawer;
import transformers.GMover;
import transformers.GResizer;
import transformers.GRotater;
import transformers.GTransformer;

public class GDrawingPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    public enum EDrawingState {
        eIdle,
        e2P,
        eNP
    }
    
    private Vector<GShape> shapes;
    private GTransformer transformer;
    private GShape currentShape;
    private GShape selectedShape;
    private boolean bUpdated;
    private EShapeTool eShapeTool;
    private EDrawingState eDrawingState;
    
    private CommandManager commandManager;
    private Vector<GShape> clipboard;
    private int nextGroupId = 1;
    
    public GDrawingPanel() {
        this.setBackground(Color.WHITE);
        
        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        
        KeyHandler keyHandler = new KeyHandler();
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        this.setRequestFocusEnabled(true);
        this.requestFocusInWindow();
        
        this.currentShape = null;
        this.selectedShape = null;
        this.shapes = new Vector<GShape>();
        this.eShapeTool = null;
        this.eDrawingState = EDrawingState.eIdle;
        this.bUpdated = false;
        
        this.commandManager = new CommandManager();
        this.clipboard = new Vector<GShape>();
    }

    public void initialize() {
        this.shapes.clear();
        this.commandManager.clear();
        this.requestFocusInWindow();
        this.repaint();
    }
    
    public Vector<GShape> getShape() {
        return this.shapes;
    }
    
    public void setShapes(Vector<GShape> shapes) {
        this.shapes = shapes;
        this.commandManager.clear();
    }
    
    public void setEShapeTool(EShapeTool eShapeTool) {
        this.eShapeTool = eShapeTool;
    }
    
    public boolean isUpdated() {
        return this.bUpdated;
    }
    
    public void setBUpdated(boolean bUpdated) {
        this.bUpdated = bUpdated;
    }
    
    public void undo() {
        if (commandManager.undo()) {
            clearAllSelection();
            setBUpdated(true);
            repaint();
        }
    }
    
    public void redo() {
        if (commandManager.redo()) {
            clearAllSelection();
            setBUpdated(true);
            repaint();
        }
    }
    
    public boolean canUndo() {
        return commandManager.canUndo();
    }
    
    public boolean canRedo() {
        return commandManager.canRedo();
    }
    
    public void deleteSelectedShapes() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.isEmpty()) {
            return;
        }
        
        DeleteCommand deleteCommand = new DeleteCommand(shapes, selected);
        commandManager.executeCommand(deleteCommand);
        
        clearAllSelection();
        setBUpdated(true);
        repaint();
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
        if (clipboard.isEmpty()) {
            return;
        }
        
        PasteCommand pasteCommand = new PasteCommand(shapes, clipboard);
        commandManager.executeCommand(pasteCommand);
        
        clearAllSelection();
        setBUpdated(true);
        repaint();
    }
    
    public void groupSelectedShapes() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.size() < 2) {
            return;
        }
        
        GroupCommand groupCommand = new GroupCommand(shapes, selected, nextGroupId++);
        commandManager.executeCommand(groupCommand);
        setBUpdated(true);
        repaint();
    }
    
    public void ungroupSelectedShape() {
        if (selectedShape == null || !selectedShape.isGrouped()) {
            return;
        }
        
        UngroupCommand ungroupCommand = new UngroupCommand(shapes, selectedShape.getGroupId());
        commandManager.executeCommand(ungroupCommand);
        setBUpdated(true);
        repaint();
    }
    
    public boolean canGroup() {
        return getSelectedShapes().size() >= 2;
    }
    
    public boolean canUngroup() {
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
    
    private void selectGroup(int groupId) {
        clearAllSelection();
        for (GShape shape : shapes) {
            if (shape.getGroupId() == groupId) {
                shape.setSelected(true);
                selectedShape = shape;
            }
        }
    }
    
    private Vector<GShape> getSelectedShapes() {
        Vector<GShape> selected = new Vector<>();
        for (GShape shape : shapes) {
            if (shape.isSelected()) {
                selected.add(shape);
            }
        }
        return selected;
    }
    
    public void bringToFront() {
        if (selectedShape == null) {
            return;
        }
        
        if (selectedShape.isGrouped()) {
            moveGroupToPosition(selectedShape.getGroupId(), shapes.size());
        } else {
            boolean removed = shapes.remove(selectedShape);
            if (removed) {
                shapes.add(selectedShape);
            }
        }
        
        setBUpdated(true);
        repaint();
    }
    
    public void sendToBack() {
        if (selectedShape == null) {
            return;
        }
        
        if (selectedShape.isGrouped()) {
            moveGroupToPosition(selectedShape.getGroupId(), 0);
        } else {
            boolean removed = shapes.remove(selectedShape);
            if (removed) {
                shapes.add(0, selectedShape);
            }
        }
        
        setBUpdated(true);
        repaint();
    }
    
    public void bringForward() {
        if (selectedShape == null) return;
        
        int currentIndex = shapes.indexOf(selectedShape);
        if (currentIndex == -1 || currentIndex >= shapes.size() - 1) return;
        
        shapes.remove(currentIndex);
        shapes.add(currentIndex + 1, selectedShape);
        setBUpdated(true);
        repaint();
    }
    
    public void sendBackward() {
        if (selectedShape == null) return;
        
        int currentIndex = shapes.indexOf(selectedShape);
        if (currentIndex == -1 || currentIndex <= 0) return;
        
        shapes.remove(currentIndex);
        shapes.add(currentIndex - 1, selectedShape);
        setBUpdated(true);
        repaint();
    }
    
    private void moveGroupToPosition(int groupId, int position) {
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
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (GShape shape : shapes) {
            shape.draw((Graphics2D) g);
        }
    }
    
    private GShape onShape(int x, int y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                return shapes.get(i);
            }
        }
        return null;
    }
    
    private void clearAllSelection() {
        for (GShape shape : shapes) {
            shape.setSelected(false);
        }
        selectedShape = null;
    }
    
    private void selectShapeOrGroup(GShape clickedShape, boolean isMultiSelect) {
        if (isMultiSelect) {
            clickedShape.setSelected(!clickedShape.isSelected());
            if (clickedShape.isSelected()) {
                selectedShape = clickedShape;
            }
        } else {
            if (clickedShape.isGrouped()) {
                selectGroup(clickedShape.getGroupId());
            } else {
                clearAllSelection();
                clickedShape.setSelected(true);
                selectedShape = clickedShape;
            }
        }
    }
    
    private void startTransform(int x, int y, boolean isMultiSelect) {
        if (eShapeTool == EShapeTool.eSelect) {
            GShape clickedShape = onShape(x, y);
            
            if (clickedShape == null) {
                if (!isMultiSelect) {
                    clearAllSelection();
                }
            } else {
                selectShapeOrGroup(clickedShape, isMultiSelect);
                
                if (!isMultiSelect && selectedShape != null) {
                    EAnchor selectedAnchor = clickedShape.getESeletedAnchor();
                    if (selectedAnchor == EAnchor.eMM) {
                        transformer = new GMover(selectedShape);
                    } else if (selectedAnchor == EAnchor.eRR) {
                        transformer = new GRotater(selectedShape);
                    } else {
                        transformer = new GResizer(selectedShape);
                    }
                    
                    transformer.setAllShapes(shapes);
                    transformer.setCommandManager(commandManager);
                    transformer.start((Graphics2D) getGraphics(), x, y);
                }
            }
        } else {
            currentShape = eShapeTool.newShape();
            transformer = new GDrawer(currentShape);
            transformer.setAllShapes(shapes);
            transformer.setCommandManager(commandManager);
            transformer.start((Graphics2D) getGraphics(), x, y);
        }
    }
    
    private void keepTransform(int x, int y) {
        if (transformer != null) {
            transformer.drag((Graphics2D) getGraphics(), x, y);
            repaint();
        }
    }
    
    private void addPoint(int x, int y) {
        if (transformer != null) {
            transformer.addPoint((Graphics2D) getGraphics(), x, y);
        }
    }
    
    private void finishTransform(int x, int y) {
        if (transformer != null) {
            transformer.finish((Graphics2D) getGraphics(), x, y);
            
            if (eShapeTool == EShapeTool.eSelect) {
                if (selectedShape == null && transformer instanceof GDrawer) {
                    shapes.remove(shapes.size() - 1);
                    for (GShape shape : shapes) {
                        if (currentShape != null && currentShape.contains(shape)) {
                            shape.setSelected(true);
                        } else {
                            shape.setSelected(false);
                        }
                    }
                }
            } else {
                if (currentShape != null) {
                    clearAllSelection();
                    currentShape.setSelected(true);
                    selectedShape = currentShape;
                }
            }
            
            setBUpdated(true);
            repaint();
        }
    }
    
    private void changeCursor(int x, int y) {
        if (eShapeTool == EShapeTool.eSelect) {
            GShape hoveredShape = onShape(x, y);
            if (hoveredShape == null) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                EAnchor eAnchor = hoveredShape.getESeletedAnchor();
                setCursor(eAnchor.getCursor());
            }
        }
    }
    
    private class KeyHandler implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    deleteSelectedShapes();
                    break;
                case KeyEvent.VK_C:
                    if (e.isControlDown()) {
                        copySelectedShapes();
                    }
                    break;
                case KeyEvent.VK_V:
                    if (e.isControlDown()) {
                        pasteShapes();
                    }
                    break;
                case KeyEvent.VK_Z:
                    if (e.isControlDown()) {
                        undo();
                    }
                    break;
                case KeyEvent.VK_Y:
                    if (e.isControlDown()) {
                        redo();
                    }
                    break;
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {}
        
        @Override
        public void keyTyped(KeyEvent e) {}
    }
    
    private class MouseHandler implements MouseListener, MouseMotionListener {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            requestFocusInWindow();
            requestFocus();
            
            if (e.getClickCount() == 1) {
                mouse1Clicked(e);
            } else if (e.getClickCount() == 2) {
                mouse2Clicked(e);
            }
        }
        
        private void mouse1Clicked(MouseEvent e) {
            boolean isMultiSelect = e.isControlDown();
            
            if (eDrawingState == EDrawingState.eIdle) {
                if (eShapeTool.getEPoints() == EPoints.e2P) {
                    startTransform(e.getX(), e.getY(), isMultiSelect);
                    if (!isMultiSelect && selectedShape != null && transformer != null) {
                        eDrawingState = EDrawingState.e2P;
                    } else if (!isMultiSelect && eShapeTool != EShapeTool.eSelect) {
                        eDrawingState = EDrawingState.e2P;
                    }
                } else if (eShapeTool.getEPoints() == EPoints.eNP) {
                    if (!isMultiSelect) {
                        startTransform(e.getX(), e.getY(), false);
                        eDrawingState = EDrawingState.eNP;
                    }
                }
            } else if (eDrawingState == EDrawingState.e2P) {
                finishTransform(e.getX(), e.getY());
                eDrawingState = EDrawingState.eIdle;
            } else if (eDrawingState == EDrawingState.eNP) {
                addPoint(e.getX(), e.getY());
            }
            
            if (isMultiSelect) {
                repaint();
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            if (eDrawingState == EDrawingState.e2P || eDrawingState == EDrawingState.eNP) {
                keepTransform(e.getX(), e.getY());
            } else if (eDrawingState == EDrawingState.eIdle) {
                changeCursor(e.getX(), e.getY());
            }
        }
        
        private void mouse2Clicked(MouseEvent e) {
            if (eDrawingState == EDrawingState.eNP) {
                finishTransform(e.getX(), e.getY());
                eDrawingState = EDrawingState.eIdle;
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {}
        
        @Override
        public void mouseDragged(MouseEvent e) {}
        
        @Override
        public void mouseReleased(MouseEvent e) {}
        
        @Override
        public void mouseEntered(MouseEvent e) {}
        
        @Override
        public void mouseExited(MouseEvent e) {}
    }
}