package curvedLineFollower;

import java.rmi.RemoteException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RMISampleProvider;

public class ColorCustom {
	
	// Attributes
	private String name;
	private float[] rgbColorValues;

	// Tags
	public final static String COLOR_AVOID = "Avoid";
	public final static String COLOR_FOLLOW = "Follow";
	public final static String COLOR_OTHER = "Other";
	
	// Constructor
	public ColorCustom(String colorName, float[] rgbColorValues){
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
	public static String getColor(float[] capturedRGBValues, ColorCustom toAvoidColor, ColorCustom toFollowColor)
	{
		// Variables
		String colorName = COLOR_OTHER;
		double lowestDistance = 1000000;
		
		// Is it the color to Follow
		double calculatedDistance = getDistance(capturedRGBValues, toFollowColor.getRgbColorValues());
		if(calculatedDistance < lowestDistance)
		{
			lowestDistance = calculatedDistance;
			colorName = toFollowColor.getName();
		}
		
		// Is it the color to Avoid
		calculatedDistance = getDistance(capturedRGBValues, toAvoidColor.getRgbColorValues());
		if(calculatedDistance < lowestDistance)
		{
			lowestDistance = calculatedDistance;
			colorName = toAvoidColor.getName();
		}
		
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
	
	// Detect the color to follow and to avoid
	// Depending on the parameter
	public static void getColorTo(RMISampleProvider sampleProvider, boolean isToFollow) throws RemoteException {
		// Variables
		int buttonClicked;
		float[] sampleRGBValue;
		
		// Display message
		LCD.clear();
		LCD.drawString((isToFollow)? "Color to Follow":"Color to Avoid", 0, 1);
		
		// Detect
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
				if(isToFollow)
					CurvedLineFollowerCustom.toFollowColor = new ColorCustom(COLOR_FOLLOW, sampleRGBValue);
				else
					CurvedLineFollowerCustom.toAvoidColor = new ColorCustom(COLOR_FOLLOW, sampleRGBValue);
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
