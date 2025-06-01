package global;

import frames.GMainFrame;

public class GMain {
	
	public static void main(String[] args) {
		//create aggregation Hierarchy
		GMainFrame mainFrame = new GMainFrame();
		//tree traverse(DFS)
		mainFrame.initialize();
	}
}
