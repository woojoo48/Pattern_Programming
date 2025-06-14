package global;

public class GConstants {
	//final = 한번 할당되면 변하지 않는 상수임을 정의
	public final class GMainFrame {
		public static final  int SCREEN_WIDTH = 1200;  // 400 → 1200으로 변경
		public static final  int SCREEN_HEIGHT = 800;  // 400 → 800으로 변경
	}
	
	// 슬라이드 패널 관련 상수 추가
	public final class GSlidePanel {
	    public static final int PANEL_WIDTH = 250;          // 썸네일 패널 너비
	    public static final int THUMBNAIL_WIDTH = 150;      // 개별 썸네일 너비
	    public static final int THUMBNAIL_HEIGHT = 100;     // 개별 썸네일 높이
	}
	
	public final class GFileMenu {
		public static final String DEFAULT_FILE_EXTENSTION = "Shape Files (*.shapes)";
		public static final String DEFAULT_FILE_EXTENSTION_TYPE = "shapes";
		public static final String DEFAULT_FILE_NAME = "default.shapes";
		public static final String DEFAULT_FILE_TYPE = ".shapes";
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
	
	public enum EFileMenuItem{
		eNew("새 파일","newPanel"),
		eOpen("열기","open"),
		eSave("저장","save"),
		eSaveAs("다른 이름으로 저장","saveAs"),
		eQuit("종료","quit");
		
		//핫키, 툴팁또한 설정해도 됨
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
