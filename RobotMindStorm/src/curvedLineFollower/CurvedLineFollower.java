package curvedLineFollower;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Time;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import lejos.utility.Delay;

public class CurvedLineFollower 
{
	public static void lookForLine(RMISampleProvider sampleProvider,EV3LargeRegulatedMotor motorGauche,EV3LargeRegulatedMotor motorDroite)throws RemoteException, MalformedURLException, NotBoundException {
		float[] sample = new float[3];
		Boolean turnDebut=true;
		long timeToTurn=500,time=System.currentTimeMillis(),tStart=time,turnTime=600,turnTimeStart=time;
		int i=0,speed=200;
		String detectedColor;
		// While the brick is detecting
		while(Button.ESCAPE.isUp())
		{
			// Detect a color and get the RGB values
			sample = sampleProvider.fetchSample();
			sample[0] = sample[0]*256f;
			sample[1] = sample[1]*256f;
			sample[2] = sample[2]*256f;
					
			// Get the name of the detected RGB value
			detectedColor = Color.getColor(sample);
					
			// Print the detected color
			LCD.drawString(detectedColor, 0, 1);
					
					
			//Akram trying to make curved line follower
			if(detectedColor.equals(Color.colorsLearned.get(1).getName())) {
				motorDroite.stop(true);
				motorGauche.stop(true);
				break;
			}else {
				time=System.currentTimeMillis();
				
				if(time-tStart>timeToTurn) {
					if(turnDebut) {
						turnTimeStart=time;
						turnDebut=false;
					}
					if(time-turnTimeStart<turnTime) {
						motorGauche.setSpeed(0);
						motorDroite.setSpeed(speed);
					}else {
						tStart=time;
						i++;
						if(i==4) {
							timeToTurn*=2;
							i=0;
						}
						turnDebut=true;
					}
				}else {
					motorGauche.setSpeed(speed);
					motorDroite.setSpeed(speed);
					
				}
				motorGauche.forward();
				motorDroite.forward();
			}
		}
		Boolean gTry=false,dTry=!gTry;
		for (int j = 0; j < 2;j++) {
			if(gTry) {
				motorGauche.setSpeed(speed/4);
				motorGauche.backward();
			}else {
				motorGauche.setSpeed(speed);
				motorGauche.forward();
			}
			if(dTry) {
				motorDroite.setSpeed(speed/4);
				motorDroite.backward(); 
			}else {
				motorDroite.setSpeed(speed);
				motorDroite.forward();
			}
			Delay.msDelay(500);
			motorGauche.stop(true);
			motorDroite.stop(true);
			sample = sampleProvider.fetchSample();
			sample[0] = sample[0]*256f;
			sample[1] = sample[1]*256f;
			sample[2] = sample[2]*256f;
			detectedColor = Color.getColor(sample);
			if(detectedColor.equals(Color.colorsLearned.get(1).getName())) break;
			if(gTry) {
				motorGauche.setSpeed(speed);
				motorGauche.forward(); 	
			}else {
				motorGauche.setSpeed(speed/4);
				motorGauche.backward();
			}
			if(dTry) {
				motorDroite.setSpeed(speed);
				motorDroite.forward(); 
			}else {
				motorDroite.setSpeed(speed/4);
				motorDroite.backward();
			}
			Delay.msDelay(500);
			motorGauche.stop(true);
			motorDroite.stop(true);
			
			gTry=!gTry;
			dTry=!dTry;
		}
	}
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException
	{
		// Variables
		float[] sample = new float[3];
		
		// Get the connected EV3Brick and the colorSensor
		RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
		RMISampleProvider sampleProvider = ev3.createSampleProvider("S4", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		
		// Get the motors left and right
		EV3LargeRegulatedMotor largeRegulatedMotorGauche = new EV3LargeRegulatedMotor(MotorPort.D);
		EV3LargeRegulatedMotor largeRegulatedMotorDroit = new EV3LargeRegulatedMotor(MotorPort.A);
		
		//speed variable
		int speed=200;
		
		// Set the speed of the motors
		largeRegulatedMotorGauche.setSpeed(speed);
		largeRegulatedMotorDroit.setSpeed(speed);
		
		// Learn the colors
		Color.learnColors();
		
		// Click enter to start following
		Button.ENTER.waitForPressAndRelease();
		
		lookForLine(sampleProvider,largeRegulatedMotorGauche,largeRegulatedMotorDroit); 
		
		long tBlack=0,tWhite=0;
		int delay=0;
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
			
			
			//Akram trying to make curved line follower
			if(detectedColor.equals(Color.colorsLearned.get(1).getName())) {
				if(tBlack==0)
					tBlack=System.currentTimeMillis();
				else if((System.currentTimeMillis()-tBlack)>500) {
					speed=300;
				}else if((System.currentTimeMillis()-tBlack)>200) {
					speed=400;
				}else {
					speed=500;
				}
				tWhite=0;
				largeRegulatedMotorGauche.setSpeed(speed);
				largeRegulatedMotorDroit.setSpeed((float)0.35*speed);
			}else {
				if(tWhite==0)
					tWhite=System.currentTimeMillis();
				else if((System.currentTimeMillis()-tWhite)>1500 && tWhite!=0 ) {
					largeRegulatedMotorDroit.stop();
					largeRegulatedMotorDroit.stop();
					lookForLine(sampleProvider, largeRegulatedMotorGauche, largeRegulatedMotorDroit);
					tWhite=0;
				}
				tBlack=0;
				
				largeRegulatedMotorGauche.setSpeed((float)0.35*speed);
				largeRegulatedMotorDroit.setSpeed(speed);
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
