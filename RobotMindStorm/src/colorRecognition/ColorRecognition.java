package colorRecognition;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;

public class ColorRecognition
{	
	// Get the name of the detected color
	static String getColor(float[] captured, ArrayList<Color> hadColors)
	{
		// Variables
		String color = "other";
		double lowestDistance = 1000000;
		
		// Calculate the distance between 
		// the detected color and 
		// the colors that we learned
		for(int i = 0; i < hadColors.size(); i++)
		{
			// Calculate the distance
			double distance = getDistance(captured, hadColors.get(i).getValues());
			
			// If the calculated distance is 
			// lower then the lowestDistance
			// then we update the lowestDistance
			// and the name of the color
			if(distance < lowestDistance)
			{
				lowestDistance = distance;
				color = hadColors.get(i).getName();
			}
		}
		
		// Return the name of the color detected
		return color;
	}
	
	// Calculate the distance between 2 colors
	static double getDistance(float[] captured, float[] had)
	{
		double x = Math.pow(captured[0] - had[0], 2);
		double y = Math.pow(captured[1] - had[1], 2);
		double z = Math.pow(captured[2] - had[2], 2);
		
		double distance = Math.sqrt(x+y+z);
		return distance;
	}
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException
	{
		// Variables
		boolean isContinue = true;
		float[] sample = new float[3];
		
		// Get the connected EV3Brick and the colorSensor
		RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
		RMISampleProvider sampleProvider = ev3.createSampleProvider("S1", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		
		// Learning
		ArrayList<Color> colorsLearned = new ArrayList<>();
		float[] lightGreen = {17.32f, 50.20f, 6.27f};
		float[] white = {61.74f, 63.75f, 35.39f};
		float[] black = {2.26f, 2.51f, 1.25f};
		float[] orange = {51.70f, 12.05f, 4.52f};
		float[] brown = {12.90f, 8.03f, 3.26f};
		float[] red = {51.45f, 14.56f, 4.77f};
		colorsLearned.add(new Color("Light Green", lightGreen));
		colorsLearned.add(new Color("White", white));
		colorsLearned.add(new Color("Black", black));
		colorsLearned.add(new Color("Orange", orange));
		colorsLearned.add(new Color("Brown", brown));
		colorsLearned.add(new Color("Red", red));
		
		
		// Clear the screen and print a message
		LCD.clear();
		LCD.drawString("Detect Colors", 0, 0);
		LCD.drawString("Press Enter To Test A Color", 0, 1);
		while(isContinue)
		{
			// Wait for a button press
			int buttonPressed = Button.waitForAnyPress();
			
			// If the button pressed is Enter
			if(buttonPressed == Button.ID_ENTER)
			{
				// Detect a color and get the RGB values
				sample = sampleProvider.fetchSample();
				sample[0] = sample[0]*256f;
				sample[1] = sample[1]*256f;
				sample[2] = sample[2]*256f;
				
				// Get the name of the detected RGB value
				String color = getColor(sample, colorsLearned);
				
				// Print the name of the detected color
				LCD.drawString(color, 0, 2);
			}
			// If the button pressed is ESCAPE
			else if(buttonPressed == Button.ID_ESCAPE)
			{
				// Stop the detection loop
				isContinue = false;
			}
		}
		
		// Stop the sensor
		sampleProvider.close();
	}
}
