package colorRecognition;

public class Color
{
	private String name; // The name of the colors
	private float[] values; // RGB values of the color
	
	public Color(String name, float[] values) {
		super();
		this.name = name;
		this.values = values;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float[] getValues() {
		return values;
	}
	public void setValues(float[] values) {
		this.values = values;
	}
	
}
