package curvedLineFollower;

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

public class CurvedLineFollower 
{
	// Attributes
	float[] sampleRGBValue;
	int motorSpeed;
	RemoteEV3 ev3;
	RMISampleProvider sampleProvider;
	EV3LargeRegulatedMotor largeRegulatedMotorGauche;
	EV3LargeRegulatedMotor largeRegulatedMotorDroit;
	
	
	// Constructor
	public CurvedLineFollower() throws RemoteException, MalformedURLException, NotBoundException {
		// RGB sample variable
		sampleRGBValue = new float[3];
		
		// Get the connected EV3Brick and the colorSensor
		ev3 = new RemoteEV3("10.0.1.1");
		sampleProvider = ev3.createSampleProvider("S4", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		
		// Get the motors left and right
		largeRegulatedMotorGauche = new EV3LargeRegulatedMotor(MotorPort.D);
		largeRegulatedMotorDroit = new EV3LargeRegulatedMotor(MotorPort.A);
		
		// Set the speed of the motors
		motorSpeed = 200;
		largeRegulatedMotorGauche.setSpeed(motorSpeed);
		largeRegulatedMotorDroit.setSpeed(motorSpeed);
	}
	
	public void lookForLine() throws RemoteException, MalformedURLException, NotBoundException {
		sampleRGBValue = new float[3];
		Boolean turnDebut = true;
		long timeToTurn = 500, time = System.currentTimeMillis(), tStart = time, turnTime = 600, turnTimeStart = time;
		int i=0,motorSpeed = 200;
		String detectedColorName;
		// While the brick is detecting
		while(Button.ESCAPE.isUp()) {
			// Detect a color and get the RGB values
			this.sampleRGBValue = sampleProvider.fetchSample();
			this.sampleRGBValue[0] = this.sampleRGBValue[0] * 256f;
			this.sampleRGBValue[1] = this.sampleRGBValue[1] * 256f;
			this.sampleRGBValue[2] = this.sampleRGBValue[2] * 256f;
					
			// Get the name of the detected RGB value
			detectedColorName = Color.getColor(this.sampleRGBValue);
					
			// Print the detected color
			LCD.drawString(detectedColorName, 0, 1);
					
			if(detectedColorName.equals(Color.COLOR_BLACK)) {
				largeRegulatedMotorDroit.stop(true);
				largeRegulatedMotorGauche.stop(true);
				break;
			}
			else {
				time = System.currentTimeMillis();
				
				if(time-tStart > timeToTurn) {
					if(turnDebut) {
						turnTimeStart = time;
						turnDebut = false;
					}
					
					if(time-turnTimeStart < turnTime) {
						largeRegulatedMotorGauche.setSpeed(0);
						largeRegulatedMotorDroit.setSpeed(motorSpeed);
					}
					else {
						tStart=time;
						i++;
						if(i == 4) {
							timeToTurn *= 2;
							i = 0;
						}
						turnDebut = true;
					}
				}
				else {
					largeRegulatedMotorGauche.setSpeed(motorSpeed);
					largeRegulatedMotorDroit.setSpeed(motorSpeed);
					
				}
				largeRegulatedMotorGauche.forward();
				largeRegulatedMotorDroit.forward();
			}
		}
		
		Boolean tryLeft=false,tryRight =!tryLeft;
		for (int j = 0; j < 2;j++) {
			if(tryLeft) {
				largeRegulatedMotorGauche.setSpeed(motorSpeed/4);
				largeRegulatedMotorGauche.backward();
			}
			else {
				largeRegulatedMotorGauche.setSpeed(motorSpeed);
				largeRegulatedMotorGauche.forward();
			}
			
			if(tryRight ) {
				largeRegulatedMotorDroit.setSpeed(motorSpeed/4);
				largeRegulatedMotorDroit.backward(); 
			}
			else {
				largeRegulatedMotorDroit.setSpeed(motorSpeed);
				largeRegulatedMotorDroit.forward();
			}
			
			Delay.msDelay(500);
			largeRegulatedMotorGauche.stop(true);
			largeRegulatedMotorDroit.stop(true);
			this.sampleRGBValue = sampleProvider.fetchSample();
			this.sampleRGBValue[0] = this.sampleRGBValue[0]*256f;
			this.sampleRGBValue[1] = this.sampleRGBValue[1]*256f;
			this.sampleRGBValue[2] = this.sampleRGBValue[2]*256f;
			detectedColorName = Color.getColor(this.sampleRGBValue);
			
			if(detectedColorName.equals(Color.learnedColors.get(1).getName()))
				break;
			
			if(tryLeft) {
				largeRegulatedMotorGauche.setSpeed(motorSpeed);
				largeRegulatedMotorGauche.forward(); 	
			}
			else {
				largeRegulatedMotorGauche.setSpeed(motorSpeed/4);
				largeRegulatedMotorGauche.backward();
			}
			
			if(tryRight ) {
				largeRegulatedMotorDroit.setSpeed(motorSpeed);
				largeRegulatedMotorDroit.forward(); 
			}
			else {
				largeRegulatedMotorDroit.setSpeed(motorSpeed/4);
				largeRegulatedMotorDroit.backward();
			}
			Delay.msDelay(500);
			largeRegulatedMotorGauche.stop(true);
			largeRegulatedMotorDroit.stop(true);
			
			tryLeft = !tryLeft;
			tryRight = !tryRight ;
		}
	}
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		// Instantiate the Brick
		CurvedLineFollower curvedLineFollower = new CurvedLineFollower();
		
		// Learn the colors
		Color.learnColors();
		
		// Click enter to start following
		Button.ENTER.waitForPressAndRelease();
		
		// Search for the line
		curvedLineFollower.lookForLine();
		
		long timeInBlack = 0,timeInWhite = 0;
		
		// While the brick is detecting
		while(Button.ESCAPE.isUp()) {
			// Detect a color and get the RGB values
			curvedLineFollower.sampleRGBValue = curvedLineFollower.sampleProvider.fetchSample();
			curvedLineFollower.sampleRGBValue[0] = curvedLineFollower.sampleRGBValue[0]*256f;
			curvedLineFollower.sampleRGBValue[1] = curvedLineFollower.sampleRGBValue[1]*256f;
			curvedLineFollower.sampleRGBValue[2] = curvedLineFollower.sampleRGBValue[2]*256f;
			
			// Get the name of the detected RGB value
			String detectedColor = Color.getColor(curvedLineFollower.sampleRGBValue);
			
			// Print the detected color
			LCD.drawString(detectedColor, 0, 1);
			
			if(detectedColor.equals(Color.COLOR_BLACK)) {
				if(timeInBlack == 0)
					timeInBlack=System.currentTimeMillis();
				else if((System.currentTimeMillis() - timeInBlack) > 500) {
					curvedLineFollower.motorSpeed = 300;
				}else if((System.currentTimeMillis() - timeInBlack) > 200) {
					curvedLineFollower.motorSpeed = 400;
				}else {
					curvedLineFollower.motorSpeed = 500;
				}
				timeInWhite = 0;
				curvedLineFollower.largeRegulatedMotorGauche.setSpeed(curvedLineFollower.motorSpeed);
				curvedLineFollower.largeRegulatedMotorDroit.setSpeed((float) 0.35 * curvedLineFollower.motorSpeed);
			}
			else {
				if(timeInWhite == 0)
					
					timeInWhite = System.currentTimeMillis();
				else if((System.currentTimeMillis() - timeInWhite) > 1500 && timeInWhite != 0 ) {
					curvedLineFollower.largeRegulatedMotorDroit.stop();
					curvedLineFollower.largeRegulatedMotorDroit.stop();
					curvedLineFollower.lookForLine();
					timeInWhite = 0;
				}
				timeInBlack = 0;
				
				curvedLineFollower.largeRegulatedMotorGauche.setSpeed((float) 0.35 * curvedLineFollower.motorSpeed);
				curvedLineFollower.largeRegulatedMotorDroit.setSpeed(curvedLineFollower.motorSpeed);
			}
			
			// Make the motors move forward
			curvedLineFollower.largeRegulatedMotorGauche.forward();
			curvedLineFollower.largeRegulatedMotorDroit.forward();
		}
		
		// Close the colorSensor and the motors
		curvedLineFollower.sampleProvider.close();
		curvedLineFollower.largeRegulatedMotorGauche.close();
		curvedLineFollower.largeRegulatedMotorDroit.close();
	}
}
