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

    // association
    private GSlideManager slideManager;
    
    public GEditMenu() {
        super("Edit");
        
        ActionHandler actionHandler = new ActionHandler();
        
        // ✨ FileMenu처럼 enum을 활용한 깔끔한 구조
        for(EEditMenuItem eMenuItem : EEditMenuItem.values()) {
            // 구분선 추가 (특정 항목들 사이에)
            if (eMenuItem == EEditMenuItem.eGroup || eMenuItem == EEditMenuItem.eBringToFront) {
                this.add(new JSeparator());
            }
            
            JMenuItem menuItem = new JMenuItem(eMenuItem.getName());
            menuItem.addActionListener(actionHandler);
            menuItem.setActionCommand(eMenuItem.name());
            this.add(menuItem);
        }
    }

    public void initialize() {
        // 초기화 작업
    }
    
    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;
    }
    
    // ===== Undo/Redo 메서드들 =====
    
    public void undo() {
        System.out.println("실행 취소");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.undo();
        }
    }
    
    public void redo() {
        System.out.println("다시 실행");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.redo();
        }
    }
    
    // ===== 그룹화 메서드들 =====
    
    public void group() {
        System.out.println("그룹화");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.groupSelectedShapes();
        }
    }
    
    public void ungroup() {
        System.out.println("그룹 해제");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.ungroupSelectedShape();
        }
    }
    
    // ===== 도형 순서 변경 메서드들 =====
    
    public void bringToFront() {
        System.out.println("맨 앞으로 가져오기");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.bringToFront();
        }
    }
    
    public void sendToBack() {
        System.out.println("맨 뒤로 보내기");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.sendToBack();
        }
    }
    
    public void bringForward() {
        System.out.println("앞으로 가져오기");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.bringForward();
        }
    }
    
    public void sendBackward() {
        System.out.println("뒤로 보내기");
        GDrawingPanel currentPanel = slideManager.getCurrentDrawingPanel();
        if (currentPanel != null) {
            currentPanel.sendBackward();
        }
    }
    
    // ===== ✨ FileMenu와 동일한 패턴의 ActionHandler =====
    
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