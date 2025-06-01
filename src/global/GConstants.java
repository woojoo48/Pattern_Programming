package global;

public class GConstants {
	//final = 한번 할당되면 변하지 않는 상수임을 정의
	public final class GMainFrame {
		public static final  int SCREEN_WIDTH = 400;
		public static final  int SCREEN_HEIGHT = 400;
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
