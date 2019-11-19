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
	static ArrayList<float[]> colorsLearned;
	
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
		RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
		RMISampleProvider sampleProvider = ev3.createSampleProvider("S4", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		float[] sample = new float[3];
		LCD.clear();
		while(Button.LEFT.isUp())
		{
			LCD.drawString("Apprentissage", 0, 0);
			LCD.drawString("Press Enter To learn new color", 0, 1);
			Button.ENTER.waitForPressAndRelease();
			sample = sampleProvider.fetchSample();
			sample[0] = sample[0]*256f;
			sample[1] = sample[1]*256f;
			sample[2] = sample[2]*256f;
			colorsLearned.add(sample);
			LCD.drawString("Color Learned Red: " , 0, 2);
			LCD.drawString("Press Right to learn another color", 0, 3);
			Button.RIGHT.waitForPress();
		}
		sampleProvider.close();
	}
}
