package straightLineFollower;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import lejos.utility.Delay;

public class StraightLineFollower
{
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException
	{
		// Variables
		float[] sample = new float[3];
		
		// Get the connected EV3Brick and the colorSensor
		RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
		RMISampleProvider sampleProvider = ev3.createSampleProvider("S4", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		
		// Get the motors left and right
		EV3LargeRegulatedMotor largeRegulatedMotorGauche = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor largeRegulatedMotorDroit = new EV3LargeRegulatedMotor(MotorPort.D);
		
		// Set the speed of the motors
		largeRegulatedMotorGauche.setSpeed(100);
		largeRegulatedMotorDroit.setSpeed(100);
		
		// Learn the colors
		Color.learnColors();
		
		// Click enter to start following
		Button.ENTER.waitForPressAndRelease();
		
		// While the brick is detecting
		while(Button.ESCAPE.isUp())
		{
			// Detect a color and get the RGB values
			sample = sampleProvider.fetchSample();
			sample[0] = sample[0]*256f;
			sample[1] = sample[1]*256f;
			sample[2] = sample[2]*256f;
			
			// Get the name of the detected RGB value
			String detectedColor = Color.getColor(sample);
			
			// Print the detected color
			LCD.drawString(detectedColor, 0, 1);
			
			// If the detected color is green
			if(detectedColor.equals(Color.colorsLearned.get(2).getName()))
			{
				largeRegulatedMotorGauche.setSpeed(100);
				largeRegulatedMotorDroit.setSpeed(50);
				
				Delay.msDelay(500);
				
				largeRegulatedMotorDroit.setSpeed(100);
				
			}
			else
			{
				// Stop the motors
				//largeRegulatedMotorGauche.stop(true);
				//largeRegulatedMotorDroit.stop(true);
				largeRegulatedMotorGauche.setSpeed(50);
			}
			
			// Make the motors move forward
			largeRegulatedMotorGauche.forward();
			largeRegulatedMotorDroit.forward();
		}
		
		// Close the colorSensor and the motors
		sampleProvider.close();
		largeRegulatedMotorGauche.close();
		largeRegulatedMotorDroit.close();
	}
}
