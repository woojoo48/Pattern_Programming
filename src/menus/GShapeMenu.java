package menus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Vector;

import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import frames.GDrawingPanel;
import global.GConstants;
import global.GConstants.EShapeMenuItem;
import shapes.GShape;
import shapes.GShape.EStrokeStyle;
import slideFrame.GSlideManager;

public class GShapeMenu extends JMenu {
    private static final long serialVersionUID = 1L;

    private GSlideManager slideManager;
    
    public GShapeMenu() {
        super(GConstants.getShapeMenuLabel());
        
        ActionHandler actionHandler = new ActionHandler();
        
        for(EShapeMenuItem eMenuItem : EShapeMenuItem.values()) {
            if (eMenuItem == EShapeMenuItem.eStrokeWidth) {
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
    
    private void updatePanelAndRepaint() {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel != null) {
            currentPanel.setBUpdated(true);
            currentPanel.repaint();
        }
    }
    
    public void setStrokeColor() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, GConstants.getShapeMessage("selectShapeFirst"));
            return;
        }
        
        Color selectedColor = showColorPalette(GConstants.getShapeMessage("strokeColorTitle"));
        if (selectedColor == null) {
            selectedColor = JColorChooser.showDialog(this, GConstants.getShapeMessage("strokeColorTitle"), Color.BLACK);
        }
        
        if (selectedColor != null) {
            applyStrokeColorToShapes(selected, selectedColor);
            updatePanelAndRepaint();
        }
    }
    
    public void setFillColor() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, GConstants.getShapeMessage("selectShapeFirst"));
            return;
        }
        
        Color selectedColor = showColorPalette(GConstants.getShapeMessage("fillColorTitle"));
        if (selectedColor == null) {
            selectedColor = JColorChooser.showDialog(this, GConstants.getShapeMessage("fillColorTitle"), Color.WHITE);
        }
        
        if (selectedColor != null) {
            applyFillColorToShapes(selected, selectedColor);
            updatePanelAndRepaint();
        }
    }
    
    public void setStrokeWidth() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, GConstants.getShapeMessage("selectShapeFirst"));
            return;
        }
        
        float selectedWidth = showStrokeWidthDialog();
        if (selectedWidth > 0) {
            applyStrokeWidthToShapes(selected, selectedWidth);
            updatePanelAndRepaint();
        }
    }
    
    public void setStrokeStyle() {
        Vector<GShape> selected = getSelectedShapes();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, GConstants.getShapeMessage("selectShapeFirst"));
            return;
        }
        
        EStrokeStyle selectedStyle = showStrokeStyleDialog();
        if (selectedStyle != null) {
            applyStrokeStyleToShapes(selected, selectedStyle);
            updatePanelAndRepaint();
        }
    }
    
    private void applyStrokeColorToShapes(Vector<GShape> shapes, Color color) {
        for (GShape shape : shapes) {
            if (shape.isGrouped()) {
                applyStrokeColorToGroup(shape.getGroupId(), color);
            } else {
                shape.setStrokeColor(color);
            }
        }
    }
    
    private void applyFillColorToShapes(Vector<GShape> shapes, Color color) {
        for (GShape shape : shapes) {
            if (shape.isGrouped()) {
                applyFillColorToGroup(shape.getGroupId(), color);
            } else {
                shape.setFillColor(color);
            }
        }
    }
    
    private void applyStrokeWidthToShapes(Vector<GShape> shapes, float width) {
        for (GShape shape : shapes) {
            if (shape.isGrouped()) {
                applyStrokeWidthToGroup(shape.getGroupId(), width);
            } else {
                shape.setStrokeWidth(width);
            }
        }
    }
    
    private void applyStrokeStyleToShapes(Vector<GShape> shapes, EStrokeStyle style) {
        for (GShape shape : shapes) {
            if (shape.isGrouped()) {
                applyStrokeStyleToGroup(shape.getGroupId(), style);
            } else {
                shape.setStrokeStyle(style);
            }
        }
    }
    
    // Group methods
    private void applyStrokeColorToGroup(int groupId, Color color) {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        for (GShape shape : currentPanel.getShape()) {
            if (shape.getGroupId() == groupId) {
                shape.setStrokeColor(color);
            }
        }
    }
    
    private void applyFillColorToGroup(int groupId, Color color) {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        for (GShape shape : currentPanel.getShape()) {
            if (shape.getGroupId() == groupId) {
                shape.setFillColor(color);
            }
        }
    }
    
    private void applyStrokeWidthToGroup(int groupId, float width) {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        for (GShape shape : currentPanel.getShape()) {
            if (shape.getGroupId() == groupId) {
                shape.setStrokeWidth(width);
            }
        }
    }
    
    private void applyStrokeStyleToGroup(int groupId, EStrokeStyle style) {
        GDrawingPanel currentPanel = getCurrentPanel();
        if (currentPanel == null) return;
        
        for (GShape shape : currentPanel.getShape()) {
            if (shape.getGroupId() == groupId) {
                shape.setStrokeStyle(style);
            }
        }
    }
    
    private Color showColorPalette(String title) {
        Map<String, Color> palette = GConstants.getAllPaletteColors();
        Map<String, String> labels = GConstants.getAllPaletteColorLabels();
        
        String[] colorNames = palette.keySet().toArray(new String[0]);
        String[] displayNames = new String[colorNames.length];
        
        for (int i = 0; i < colorNames.length; i++) {
            displayNames[i] = labels.get(colorNames[i]);
        }
        
        String selectedName = (String) JOptionPane.showInputDialog(
            this,
            title,
            GConstants.getShapeMessage("colorPaletteTitle"),
            JOptionPane.PLAIN_MESSAGE,
            null,
            displayNames,
            displayNames[0]
        );
        
        if (selectedName != null) {
            for (String colorName : colorNames) {
                if (labels.get(colorName).equals(selectedName)) {
                    return palette.get(colorName);
                }
            }
        }
        
        return null;
    }
    
    private float showStrokeWidthDialog() {
        Map<String, Float> widths = GConstants.getAllStrokeWidthValues();
        Map<String, String> labels = GConstants.getAllStrokeWidthLabels();
        
        String[] widthNames = widths.keySet().toArray(new String[0]);
        String[] displayNames = new String[widthNames.length];
        
        for (int i = 0; i < widthNames.length; i++) {
            displayNames[i] = labels.get(widthNames[i]);
        }
        
        String selectedName = (String) JOptionPane.showInputDialog(
            this,
            GConstants.getShapeMessage("strokeWidthMessage"),
            GConstants.getShapeMessage("strokeWidthTitle"),
            JOptionPane.PLAIN_MESSAGE,
            null,
            displayNames,
            displayNames[0]
        );
        
        if (selectedName != null) {
            for (String widthName : widthNames) {
                if (labels.get(widthName).equals(selectedName)) {
                    return widths.get(widthName);
                }
            }
        }
        
        return -1;
    }
    
    private EStrokeStyle showStrokeStyleDialog() {
        Map<String, String> styleLabels = GConstants.getAllStrokeStyleLabels();
        
        String[] styleNames = styleLabels.keySet().toArray(new String[0]);
        String[] displayNames = new String[styleNames.length];
        
        for (int i = 0; i < styleNames.length; i++) {
            displayNames[i] = styleLabels.get(styleNames[i]);
        }
        
        String selectedName = (String) JOptionPane.showInputDialog(
            this,
            GConstants.getShapeMessage("strokeStyleMessage"),
            GConstants.getShapeMessage("strokeStyleTitle"),
            JOptionPane.PLAIN_MESSAGE,
            null,
            displayNames,
            displayNames[0]
        );
        
        if (selectedName != null) {
            for (String styleName : styleNames) {
                if (styleLabels.get(styleName).equals(selectedName)) {
                    return EStrokeStyle.fromString(styleName);
                }
            }
        }
        
        return null;
    }
    
    public boolean canApplyStyle() {
        return !getSelectedShapes().isEmpty();
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
            EShapeMenuItem eShapeMenuItem = EShapeMenuItem.valueOf(event.getActionCommand());
            invokeMethod(eShapeMenuItem.getMethodName());
        }
    }
}