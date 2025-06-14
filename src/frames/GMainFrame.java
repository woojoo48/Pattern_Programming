package frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;

import javax.swing.JFrame;

import global.GConstants;
import slideFrame.GSlideManager;

public class GMainFrame extends JFrame {
    //attributes
    private static final long serialVersionUID = 1L;
    
    //components
    private GMenuBar menuBar;
    private GShapeToolBar toolBar;
    private GSlideManager slideManager;  // JPanel을 상속받은 슬라이드매니저
    
    //association
    
    public GMainFrame() {
        //attributes
        this.setSize(GConstants.GMainFrame.SCREEN_WIDTH, GConstants.GMainFrame.SCREEN_HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("PowerPoint Style Drawing Program");
        
        // 화면 정중앙에 위치시키기
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        x = Math.max(0, x);
        y = Math.max(0, y);
        this.setLocation(x, y);
        
        //components
        LayoutManager layout = new BorderLayout();
        this.setLayout(layout);
        
        this.menuBar = new GMenuBar();
        this.setJMenuBar(menuBar);
        
        this.slideManager = new GSlideManager();  // JPanel을 상속받은 슬라이드매니저
        this.toolBar = new GShapeToolBar(slideManager.getCurrentDrawingPanel());
        
        // 레이아웃 배치 (기존 스타일 유지)
        this.add(toolBar, BorderLayout.NORTH);
        this.add(slideManager, BorderLayout.CENTER);  // 슬라이드매니저를 중앙 전체에 배치
    }

    public void initialize() {
        // associate (연결 작업)
        this.menuBar.associate(this.slideManager.getCurrentDrawingPanel());
        this.toolBar.associate(this.slideManager.getCurrentDrawingPanel());
        
        //associated attributes
        this.setVisible(true);
        
        // tree traverse(DFS) - aggregation hierarchy 초기화
        this.menuBar.initialize();
        this.toolBar.initialize();
        this.slideManager.initialize();  // 슬라이드 매니저의 하위 컴포넌트들 초기화
    }
    
    // 접근자 메서드들 (슬라이드 매니저를 통한 접근)
    public GSlideManager getSlideManager() {
        return this.slideManager;
    }
    
    public GDrawingPanel getCurrentDrawingPanel() {
        return this.slideManager.getCurrentDrawingPanel();
    }
}