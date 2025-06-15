package frames;

import java.util.Arrays;
import global.GConstants;

public enum Shape {
	Rect,
	Tri,
	Oval,
	Poly,
	Text;

	public String getName() {
		switch(this) {
			case Rect: return GConstants.getShapeToolLabel("eRectangle");
			case Tri: return "Triangle";
			case Oval: return GConstants.getShapeToolLabel("eEllipse"); 
			case Poly: return GConstants.getShapeToolLabel("ePolygon");
			case Text: return "TextBox";
			default: return this.name();
		}
	}
	
	public static Shape of(String str) {
		return Arrays.stream(values())
				.filter(s -> s.getName().equals(str))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unexpected enum: " + str));
	}
}