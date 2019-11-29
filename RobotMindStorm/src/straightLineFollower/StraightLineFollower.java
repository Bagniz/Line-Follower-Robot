package straightLineFollower;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;

public class StraightLineFollower
{
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException
	{
		// Variables
		boolean isDetecting = true;
		float[] sample = new float[3];
		
		// Get the connected EV3Brick and the colorSensor
		RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
		RMISampleProvider sampleProvider = ev3.createSampleProvider("S1", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		
		
		// Get the motors left and right
		EV3LargeRegulatedMotor largeRegulatedMotorGauche = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor largeRegulatedMotorDroit = new EV3LargeRegulatedMotor(MotorPort.D);
		
		// Set the speed of the motors
		largeRegulatedMotorGauche.setSpeed(400);
		largeRegulatedMotorDroit.setSpeed(400);
		
		// While the brick is detecting
		while(isDetecting)
		{
			// Detect a color and get the RGB values
			sample = sampleProvider.fetchSample();
			sample[0] = sample[0]*256f;
			sample[1] = sample[1]*256f;
			sample[2] = sample[2]*256f;
			
			// Get the name of the detected RGB value
			String detectedColor = Color.getColor(sample);
			
			// If the detected color is green
			if(detectedColor.equals(Color.colorsLearned.get(0).getName()))
			{
				// Make the motors move forward
				largeRegulatedMotorGauche.forward();
				largeRegulatedMotorDroit.forward();
			}
			else
			{
				// Stop the motors
				largeRegulatedMotorGauche.stop();
				largeRegulatedMotorDroit.stop();
			}
		}
		
		// Close the colorSensor and the motors
		sampleProvider.close();
		largeRegulatedMotorGauche.close();
		largeRegulatedMotorDroit.close();
	}
}
