package straightLineFollower;

import java.util.ArrayList;

public class Color
{
	public static ArrayList<Color> colorsLearned = new ArrayList<Color>(); // Learned colors
	private String name; // The name of the colors
	private float[] values; // RGB values of the color
	
	public Color(String name, float[] values)
	{
		super();
		this.name = name;
		this.values = values;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public float[] getValues()
	{
		return values;
	}
	
	public void setValues(float[] values)
	{
		this.values = values;
	}
	
	// Get the name of the detected color
	public static String getColor(float[] captured)
	{
		// Variables
		String color = "other";
		double lowestDistance = 1000000;
		
		// Calculate the distance between 
		// the detected color and 
		// the colors that we learned
		for(int i = 0; i < colorsLearned.size(); i++)
		{
			// Calculate the distance
			double distance = getDistance(captured, colorsLearned.get(i).getValues());
			
			// If the calculated distance is 
			// lower then the lowestDistance
			// then we update the lowestDistance
			// and the name of the color
			if(distance < lowestDistance)
			{
				lowestDistance = distance;
				color = colorsLearned.get(i).getName();
			}
		}
		
		// Return the name of the color detected
		return color;
	}
	
	// Calculate the distance between 2 colors
	public static double getDistance(float[] captured, float[] had)
	{
		double x = Math.pow(captured[0] - had[0], 2);
		double y = Math.pow(captured[1] - had[1], 2);
		double z = Math.pow(captured[2] - had[2], 2);
		
		double distance = Math.sqrt(x+y+z);
		return distance;
	}
}