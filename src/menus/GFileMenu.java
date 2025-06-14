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

    // ✨ 변경: DrawingPanel 대신 SlideManager와 연결
    private GSlideManager slideManager;
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
                "Presentation Files (*.presentation)", 
                "presentation");  // ✨ 확장자 변경
        this.fileChooser.setFileFilter(filter);
        this.fileChooser.setSelectedFile(new File("presentation.presentation"));
    }
    
    // ✨ 새로운 associate 메서드
    public void associate(GSlideManager slideManager) {
        this.slideManager = slideManager;
    }
    
    // 기존 메서드는 호환성을 위해 유지
    public void associate(GDrawingPanel drawingPanel) {
        // 레거시 지원
    }

    // ===== 파일 메뉴 메서드들 =====
    
    public void newPanel() {
        System.out.println("newPanel");
        if(this.close()) {
            this.slideManager.newPresentation();  // ✨ 전체 프레젠테이션 초기화
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

                    // ✨ 전체 슬라이드 리스트 로드
                    @SuppressWarnings("unchecked")
                    Vector<GSlide> loadedSlides = (Vector<GSlide>) objectInputStream.readObject();
                    objectInputStream.close();
                        
                    this.slideManager.loadAllSlides(loadedSlides);  // ✨ 전체 슬라이드 로드
                                
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("파일 열기 실패: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("파일 열기 취소");
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
        System.out.println("다른 이름으로 저장");
        
        boolean bCancel = false;

        int result = this.fileChooser.showSaveDialog(this.slideManager);
        
        if(result == JFileChooser.APPROVE_OPTION) {
            this.loadFileChooser();
            
            if (!file.getName().toLowerCase().endsWith(".presentation")) {
                file = new File(file.getAbsolutePath() + ".presentation");
            }
            bCancel = this.saveToFile();
        } else {
            bCancel = true;
            System.out.println("저장 취소");
        }
        return bCancel;
    }
    
    private boolean saveToFile() {
        try {
            System.out.println("파일 저장 중...");
            
            // ✨ 전체 슬라이드 저장
            Vector<GSlide> allSlides = this.slideManager.getAllSlides();
            
            FileOutputStream fileOutputStream = new FileOutputStream(this.file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            
            objectOutputStream.writeObject(allSlides);  // ✨ 전체 슬라이드 저장
            objectOutputStream.close();
            
            this.slideManager.setModified(false);  // ✨ 전체 프레젠테이션 저장 상태로 변경
            return false;
        } catch (IOException e) {
            System.out.println("저장 실패");
            e.printStackTrace();
            return true;
        }
    }
    
    public void quit() {
        System.out.println("프로그램 종료");
        if(this.close()) {
            System.exit(0);
        }
    }
    
    public boolean close() {
        boolean bCancel = false;
        
        if(this.slideManager.isModified()) {  // ✨ 전체 프레젠테이션 수정 여부 확인
            int reply = JOptionPane.showConfirmDialog(
                this.slideManager, 
                "변경내용을 저장 할까요?"
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
    
    // ActionHandler
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