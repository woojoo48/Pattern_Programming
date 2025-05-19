package frames;


import java.util.Arrays;

public enum Shape {
	Rect("rectangle"),
	Tri("Triangle"),
	Oval("Oval"),
	Poly("Polygon"),
	Text("TextBox");
	
	private final String name;

	Shape(String string) {
		this.name = string;
	}
	
	public static Shape of(String str) {
		return Arrays.stream(values())
				.filter(s -> s.name.equals(str))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unexpected emum: " + str));
	}
	
}