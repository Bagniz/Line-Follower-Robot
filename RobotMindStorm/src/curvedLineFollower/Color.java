package curvedLineFollower;

import java.rmi.RemoteException;
import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RMISampleProvider;

public class Color {
	// Attributes
	private String name;
	private float[] rgbColorValues;
	public static ArrayList<Color> learnedColors = new ArrayList<Color>();
	
	static Color toFollow,toAvoid;
	
	public final static String COLOR_BLACK = "Black";
    public final static String COLOR_WHITE = "White";
    public final static String COLOR_ORANGE = "Orange";
    public final static String COLOR_LIGHT_GREEN = "Green";
    public final static String COLOR_RED = "Red";
    public final static String COLOR_BROWN = "Brown";
    public final static String COLOR_OTHER = "Other";
	
	public Color(String colorName, float[] rgbColorValues){
		super();
		this.name = colorName;
		this.rgbColorValues = rgbColorValues;
	}
	
	// Getters and Setters
    public String getName(){
        return this.name;
    }

    public void setName(String colorName){
        this.name = colorName;
    }

    public float[] getRgbColorValues(){
        return this.rgbColorValues;
    }

    public void setRgbColorValues(float[] rgbColorValues){
        this.rgbColorValues = rgbColorValues;
    }
	
	// Get the name of the detected color
	public static String getColor(float[] capturedRGBValues)
	{
		// Variables
		String colorName = COLOR_OTHER;
		double lowestDistance = 1000000;
		
		double calculatedDistance = getDistance(capturedRGBValues, toFollow.getRgbColorValues());
		if(calculatedDistance < lowestDistance)
		{
			lowestDistance = calculatedDistance;
			colorName = toFollow.getName();
		}
		calculatedDistance = getDistance(capturedRGBValues, toAvoid.getRgbColorValues());
		if(calculatedDistance < lowestDistance)
		{
			lowestDistance = calculatedDistance;
			colorName = toAvoid.getName();
		}
		// Calculate the distance between 
		// the detected color and 
		// the colors that we learned
		/*for(Color learnedColor : learnedColors)
		{
			// Calculate the distance
			double calculatedDistance = getDistance(capturedRGBValues, learnedColor.rgbColorValues);
			
			// If the calculated distance is 
			// lower then the lowestDistance
			// then we update the lowestDistance
			// and the name of the color
			if(calculatedDistance < lowestDistance)
			{
				lowestDistance = calculatedDistance;
				colorName = learnedColor.getName();
			}
		}*/
		
		// Return the name of the color detected
		return colorName;
	}
	
	// Calculate the distance between 2 colors
	public static double getDistance(float[] capturedRGBValues, float[] knownRGBValues)
	{
		double x = Math.pow(capturedRGBValues[0] - knownRGBValues[0], 2);
		double y = Math.pow(capturedRGBValues[1] - knownRGBValues[1], 2);
		double z = Math.pow(capturedRGBValues[2] - knownRGBValues[2], 2);
		
		return Math.sqrt(x+y+z);
	}
	
	// Learn the colors
	public static void learnColor()
	{
		float[] white = {61.74f, 63.75f, 35.39f};
		float[] lightGreen = {17.32f, 50.20f, 6.27f};
		/*
		float[] orange = {51.70f, 12.05f, 4.52f};
		float[] black = {2.26f, 2.51f, 1.25f};
		float[] brown = {12.90f, 8.03f, 3.26f};
		float[] red = {51.45f, 14.56f, 4.77f};
		*/
		learnedColors.add(new Color(COLOR_WHITE, white));
		learnedColors.add(new Color(COLOR_LIGHT_GREEN, lightGreen));
		/*
		learnedColors.add(new Color(COLOR_BLACK, black));
		learnedColors.add(new Color(COLOR_ORANGE, orange));
		learnedColors.add(new Color(COLOR_BROWN, brown));
		learnedColors.add(new Color(COLOR_RED, red));
		*/
	}
	static void getColorTo(RMISampleProvider sampleProvider,boolean toFoll) throws RemoteException {
		// Variables
		int buttonClicked;
		float[] sampleRGBValue;
		
		LCD.clear();
		if(toFoll)
			LCD.drawString("Color to Follow", 0, 1);
		else
			LCD.drawString("Color to Avoid", 0, 1);

		
		while(true) {
			// Wait for the button press
			buttonClicked = Button.waitForAnyPress();
			
			// Is it the Enter button
			if(buttonClicked == Button.ID_ENTER) {
				// Detect a color and get the RGB values
				sampleRGBValue = sampleProvider.fetchSample();
				sampleRGBValue[0] = sampleRGBValue[0] * 256f;
				sampleRGBValue[1] = sampleRGBValue[1] * 256f;
				sampleRGBValue[2] = sampleRGBValue[2] * 256f;
				
				// Create the color to follow
				LCD.drawString("Color detected", 0, 2);
				if(toFoll)
					Color.toFollow=new Color("toFollow", sampleRGBValue);
				else
					Color.toAvoid=new Color("toAvoid", sampleRGBValue);
				break;
			}
			// Is it the escape button
			else if(buttonClicked == Button.ID_ESCAPE) {
				break;
			}	
		}
		LCD.clear();
	}
}
