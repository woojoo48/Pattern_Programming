package frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import frames.GShapeToolBar.EShapeTool;
import global.GConstants.EEditMenuItem;
import global.GConstants.EShapeMenuItem;
import menus.GEditMenu;
import menus.GShapeMenu;
import shapes.GRectangle;
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
    //constants
    private Vector<GShape> shapes;
    private GTransformer transformer;
    private GShape currentShape;
    private GShape selectedShape;
    private boolean bUpdated;
    private EShapeTool eShapeTool;
    private EDrawingState eDrawingState;
    
    private JPopupMenu contextMenu;
    private GEditMenu editMenu; 
    private GShapeMenu shapeMenu;
    
    //constructor
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
        
        this.contextMenu = createContextMenu();
    }

    public void initialize() {
        this.shapes.clear();
        if (this.editMenu != null) {
            this.editMenu.getCommandManager().clear();
        }
        this.requestFocusInWindow();
        this.repaint();
    }
    //getter and setter
    
    public void setEditMenu(GEditMenu editMenu) {
        this.editMenu = editMenu;
        updateTransformerCommandManager();
    }
    
    public void setShapeMenu(GShapeMenu shapeMenu) {
        this.shapeMenu = shapeMenu;
    }
    
    private void updateTransformerCommandManager() {
        if (transformer != null && editMenu != null) {
            transformer.setCommandManager(editMenu.getCommandManager());
        }
    }
    
    private JPopupMenu createContextMenu() {
        JPopupMenu popup = new JPopupMenu();
        ContextMenuHandler handler = new ContextMenuHandler();
        
        // Edit menu items
        for (EEditMenuItem menuItem : EEditMenuItem.values()) {
            if (menuItem == EEditMenuItem.eCopy || 
                menuItem == EEditMenuItem.eGroup || 
                menuItem == EEditMenuItem.eBringToFront) {
                popup.add(new JSeparator());
            }
            
            JMenuItem item = new JMenuItem(menuItem.getName());
            item.setActionCommand("EDIT_" + menuItem.name());
            item.addActionListener(handler);
            popup.add(item);
        }
        
        popup.add(new JSeparator());
        for (EShapeMenuItem menuItem : EShapeMenuItem.values()) {
            if (menuItem == EShapeMenuItem.eStrokeWidth) {
                popup.add(new JSeparator());
            }
            
            JMenuItem item = new JMenuItem(menuItem.getName());
            item.setActionCommand("SHAPE_" + menuItem.name());
            item.addActionListener(handler);
            popup.add(item);
        }
        
        return popup;
    }
    
    private void showContextMenu(int x, int y) {
        updateContextMenuState();
        contextMenu.show(this, x, y);
    }
    
    private void updateContextMenuState() {
        if (editMenu == null && shapeMenu == null) return;
        
        for (int i = 0; i < contextMenu.getComponentCount(); i++) {
            if (contextMenu.getComponent(i) instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) contextMenu.getComponent(i);
                String command = item.getActionCommand();
                
                if (command != null) {
                    if (command.startsWith("EDIT_")) {
                        String editCommand = command.substring(5); 
                        EEditMenuItem menuType = EEditMenuItem.valueOf(editCommand);
                        item.setEnabled(isEditMenuItemEnabled(menuType));
                    } else if (command.startsWith("SHAPE_")) {
                        String shapeCommand = command.substring(6);
                        EShapeMenuItem menuType = EShapeMenuItem.valueOf(shapeCommand);
                        item.setEnabled(isShapeMenuItemEnabled(menuType));
                    }
                }
            }
        }
    }
    
    private boolean isEditMenuItemEnabled(EEditMenuItem menuType) {
        if (editMenu == null) return false;
        
        switch (menuType) {
            case eUndo: return editMenu.canUndo();
            case eRedo: return editMenu.canRedo();
            case eCopy: return editMenu.canCopy();
            case ePaste: return editMenu.canPaste();
            case eDelete: return editMenu.canDelete();
            case eGroup: return editMenu.canGroup();
            case eUngroup: return editMenu.canUngroup();
            case eBringToFront:
            case eSendToBack:
            case eBringForward:
            case eSendBackward: 
                return selectedShape != null;
            default: return true;
        }
    }
    
    private boolean isShapeMenuItemEnabled(EShapeMenuItem menuType) {
        if (shapeMenu == null) return false;
        return shapeMenu.canApplyStyle();
    }
    // Getter/Setter methods - 데이터 접근만 제공
    public Vector<GShape> getShape() {
        return this.shapes;
    }
    
    public void setShapes(Vector<GShape> shapes) {
        this.shapes = shapes;
        if (this.editMenu != null) {
            this.editMenu.getCommandManager().clear();
        }
    }
    
    public GShape getSelectedShape() {
        return this.selectedShape;
    }
    
    public void setSelectedShape(GShape selectedShape) {
        this.selectedShape = selectedShape;
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
    
    private void clearAllSelection() {
        for (GShape shape : shapes) {
            shape.setSelected(false);
        }
        selectedShape = null;
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
    
    //transform
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
    
    private void startTransform(int x, int y, boolean isMultiSelect) {
        if (eShapeTool == EShapeTool.eSelect) {
            GShape clickedShape = onShape(x, y);
            
            if (clickedShape == null) {
                if (!isMultiSelect) {
                    clearAllSelection();
                    currentShape = new GRectangle();
                    transformer = new GDrawer(currentShape);
                    transformer.setAllShapes(shapes);
                    if (editMenu != null) {
                        transformer.setCommandManager(editMenu.getCommandManager());
                    }
                    transformer.start((Graphics2D) getGraphics(), x, y);
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
                    if (editMenu != null) {
                        transformer.setCommandManager(editMenu.getCommandManager());
                    }
                    transformer.start((Graphics2D) getGraphics(), x, y);
                }
            }
        } else {
            currentShape = eShapeTool.newShape();
            transformer = new GDrawer(currentShape);
            transformer.setAllShapes(shapes);
            if (editMenu != null) {
                transformer.setCommandManager(editMenu.getCommandManager());
            }
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
                    clearAllSelection();
                    
                    for (GShape shape : shapes) {
                        if (currentShape != null && currentShape.getTransformedShape().intersects(shape.getTransformedShape().getBounds())) {
                            shape.setSelected(true);
                            selectedShape = shape;
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
    
    private class ContextMenuHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            
            if (command.startsWith("EDIT_")) {
                if (editMenu == null) return;
                
                String editCommand = command.substring(5);
                EEditMenuItem menuItem = EEditMenuItem.valueOf(editCommand);
                
                switch (menuItem) {
                    case eUndo: editMenu.undo(); break;
                    case eRedo: editMenu.redo(); break;
                    case eCopy: editMenu.copySelectedShapes(); break;
                    case ePaste: editMenu.pasteShapes(); break;
                    case eDelete: editMenu.deleteSelectedShapes(); break;
                    case eGroup: editMenu.group(); break;
                    case eUngroup: editMenu.ungroup(); break;
                    case eBringToFront: editMenu.bringToFront(); break;
                    case eSendToBack: editMenu.sendToBack(); break;
                    case eBringForward: editMenu.bringForward(); break;
                    case eSendBackward: editMenu.sendBackward(); break;
                }
            } else if (command.startsWith("SHAPE_")) {
                if (shapeMenu == null) return;
                
                String shapeCommand = command.substring(6); // Remove "SHAPE_" prefix
                EShapeMenuItem menuItem = EShapeMenuItem.valueOf(shapeCommand);
                
                switch (menuItem) {
                    case eStrokeColor: shapeMenu.setStrokeColor(); break;
                    case eFillColor: shapeMenu.setFillColor(); break;
                    case eStrokeWidth: shapeMenu.setStrokeWidth(); break;
                    case eStrokeStyle: shapeMenu.setStrokeStyle(); break;
                }
            }
        }
    }
    
    private class KeyHandler implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            if (editMenu == null) return;
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    editMenu.deleteSelectedShapes();
                    break;
                case KeyEvent.VK_C:
                    if (e.isControlDown()) {
                        editMenu.copySelectedShapes();
                    }
                    break;
                case KeyEvent.VK_V:
                    if (e.isControlDown()) {
                        editMenu.pasteShapes();
                    }
                    break;
                case KeyEvent.VK_Z:
                    if (e.isControlDown()) {
                        editMenu.undo();
                    }
                    break;
                case KeyEvent.VK_Y:
                    if (e.isControlDown()) {
                        editMenu.redo();
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
            
            if (SwingUtilities.isRightMouseButton(e)) {
                showContextMenu(e.getX(), e.getY());
                return;
            }
            
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
                    if (!isMultiSelect && (selectedShape != null && transformer != null) || 
                        (eShapeTool == EShapeTool.eSelect && transformer instanceof GDrawer)) {
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