package frames;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;

import slideFrame.GSlideManager;  // 추가!
import shapes.GEllipse;
import shapes.GLine;
import shapes.GPolygon;
import shapes.GRectangle;
import shapes.GShape;
import shapes.GShape.EPoints;

public class GShapeToolBar extends JToolBar{
    private static final long serialVersionUID = 1L;

    public enum EShapeTool {
        eSelect("select",EPoints.e2P, GRectangle.class),
        eRectangle("rectangle",EPoints.e2P, GRectangle.class),
        eEllipse("ellipse",EPoints.e2P,GEllipse.class),
        eLine("line",EPoints.e2P,GLine.class),
        ePolygon("polygon",EPoints.eNP,GPolygon.class);
        
        private String name;
        private EPoints ePoints;
        private Class<? extends GShape> classShape;
        private EShapeTool(String name,EPoints eDrawingType, Class<? extends GShape> gShape) {
            this.name = name;
            this.ePoints = eDrawingType;
            this.classShape = gShape;
        }
        public String getName() {
            return this.name;
        }
        
        public EPoints getEPoints() {
            return this.ePoints;
        }
        
        public GShape newShape(){
            try {
                GShape shape = (GShape) classShape.getConstructor().newInstance();
                return shape;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    private GSlideManager slideManager;
    
    public GShapeToolBar(GDrawingPanel gDrawingPanel) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        addToolbarButton();
    }

    private void addToolbarButton() {
        ButtonGroup group = new ButtonGroup();

        for (EShapeTool eShapeType : EShapeTool.values()) {
            JRadioButton button = new JRadioButton(eShapeType.getName());
            ActionListener actionListener = new ActionHandler();
            button.addActionListener(actionListener);
            button.setActionCommand(eShapeType.toString()); 
            group.add(button);
            this.add(button); 
        }
    }
    
    public void initialize() {
        JRadioButton button = (JRadioButton) this.getComponent(EShapeTool.eSelect.ordinal());
        button.doClick();
    }

    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;
    }
    
    public void associate(GDrawingPanel drawingPanel) {
    }

    private class ActionHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String sShapeType = e.getActionCommand();
            EShapeTool eShapeType = EShapeTool.valueOf(sShapeType);
            
            if (slideManager != null) {
                slideManager.setCurrentTool(eShapeType);
            }
        }
    }
}
