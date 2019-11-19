package colorRecognition;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.Button;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import lejos.utility.Delay;

public class ColorRecognition
{
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException
	{
		RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
		RMISampleProvider sampleProvider = ev3.createSampleProvider("S4", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		
		float[] samples = new float[3];
		
		while(Button.ENTER.isUp())
		{
			samples = sampleProvider.fetchSample();
			System.out.println(samples[0] + " / " + samples[1] + " / " + samples[2]);
			Delay.msDelay(100);	
		}
		sampleProvider.close();
	}
}
