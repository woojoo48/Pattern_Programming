package global;

public class GConstants {
    public final class GMainFrame {
        public static final int SCREEN_WIDTH = 1200;
        public static final int SCREEN_HEIGHT = 800;
    }
    
    public final class GSlidePanel {
        public static final int PANEL_WIDTH = 250;
        public static final int THUMBNAIL_WIDTH = 150;
        public static final int THUMBNAIL_HEIGHT = 100;
    }
    
    public final class GFileMenu {
        // ✨ 프레젠테이션 파일로 변경
        public static final String DEFAULT_FILE_EXTENSTION = "Presentation Files (*.presentation)";
        public static final String DEFAULT_FILE_EXTENSTION_TYPE = "presentation";
        public static final String DEFAULT_FILE_NAME = "presentation.presentation";
        public static final String DEFAULT_FILE_TYPE = ".presentation";
        public static final String DEFAULT_FILE_ROOT = "C:\\Users\\wjddn\\eclipse-workspace\\Pattern_asssignment";

        public static final String SAVE_OPTION_MSG = "변경내용을 저장 할까요?";
        public static final String SAVE_MSG = "save";
        public static final String OVERWRITE_OPTION_MSG = "File already exist. Overwrite?";
        public static final String OVERWRITE_MSG = "Overwrite";
        public static final String CANCEL = "cancel";
        public static final String SAVE_AS = "save As";
        public static final String SAVE = "save";
        public static final String SAVE_NOT = "not save";
        public static final String OPEN = "open : ";
        public static final String OPEN_NOT = "Not Open";
    }
    
    public enum EEditMenuItem {
        // Undo/Redo (첫 번째 그룹)
        eUndo("실행 취소", "undo"),
        eRedo("다시 실행", "redo"),
        
        // 그룹화 (두 번째 그룹) - eGroup 앞에 구분선
        eGroup("그룹화", "group"),
        eUngroup("그룹 해제", "ungroup"),
        
        // 도형 순서 변경 (세 번째 그룹) - eBringToFront 앞에 구분선
        eBringToFront("맨 앞으로 가져오기", "bringToFront"),
        eSendToBack("맨 뒤로 보내기", "sendToBack"),
        eBringForward("앞으로 가져오기", "bringForward"),
        eSendBackward("뒤로 보내기", "sendBackward");
        
        private String name;
        private String methodName;
        
        private EEditMenuItem(String name, String methodName) {
            this.name = name;
            this.methodName = methodName;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getMethodName() {
            return this.methodName;
        }
    }
    
    public enum EFileMenuItem{
        eNew("새 파일","newPanel"),
        eOpen("열기","open"),
        eSave("저장","save"),
        eSaveAs("다른 이름으로 저장","saveAs"),
        eQuit("종료","quit");
        
        private String name;
        private String methodName;
        private EFileMenuItem(String name,String methodName) {
            this.name = name;
            this.methodName = methodName;
        }
        public String getName() {
            return this.name;
        }
        
        public String getMethodName() {
            return this.methodName;
        }
    }
}