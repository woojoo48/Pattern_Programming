package global;

public class GConstants {
	//final = 한번 할당되면 변하지 않는 상수임을 정의
	public final class GMainFrame {
		public static final  int SCREEN_WIDTH = 400;
		public static final  int SCREEN_HEIGHT = 400;
	}
	
	public final class GFileMenu {
		public static final String DEFAULT_FILE_EXTENSTION = "Shape Files (*.shapes)";
		public static final String DEFAULT_FILE_EXTENSTION_TYPE = "shapes";
		public static final String DEFAULT_FILE_NAME = "default.shapes";
		public static final String DEFAULT_FILE_TYPE = ".shapes";

		public static final String SAVE_OPTION_MSG = "Do you want to save the file before opening it?";
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
		ePrint("인쇄","print"),
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
