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
	static Color toAvoidColor, toFollowColor;
	
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
			detectedColorName = Color.getColor(this.sampleRGBValue, toAvoidColor, toFollowColor);
					
			// Print the detected color
			LCD.drawString(detectedColorName, 0, 1);
					
			if(detectedColorName.equals(toFollowColor.getName())) {
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
		
		while(Button.ESCAPE.isUp()) {	
			
			largeRegulatedMotorGauche.setSpeed(motorSpeed);
			largeRegulatedMotorGauche.forward();
			largeRegulatedMotorDroit.setSpeed(motorSpeed);
			largeRegulatedMotorDroit.backward();
			
			this.sampleRGBValue = sampleProvider.fetchSample();
			this.sampleRGBValue[0] = this.sampleRGBValue[0]*256f;
			this.sampleRGBValue[1] = this.sampleRGBValue[1]*256f;
			this.sampleRGBValue[2] = this.sampleRGBValue[2]*256f;
			detectedColorName = Color.getColor(this.sampleRGBValue, toAvoidColor, toFollowColor);
			
			if(detectedColorName.equals(toAvoidColor.getName())) {
				Delay.msDelay(300);
				largeRegulatedMotorGauche.stop(true);
				largeRegulatedMotorDroit.stop(true);
				return;
			}
		}
	}
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		// Instantiate the Brick
		CurvedLineFollower curvedLineFollower = new CurvedLineFollower();
		
		// Learn the color to follow
		Color.getColorTo(curvedLineFollower.sampleProvider, true);
		
		// Learn the color to avoid
		Color.getColorTo(curvedLineFollower.sampleProvider, false);
		
		// Click enter to start following
		Button.ENTER.waitForPressAndRelease();
		
		// Search for the line
		curvedLineFollower.lookForLine();
		
		long timeInBlack = 0,timeInWhite = 0;
		float angle=0.4f;
		
		// While the brick is detecting
		while(Button.ESCAPE.isUp()) {
			// Detect a color and get the RGB values
			curvedLineFollower.sampleRGBValue = curvedLineFollower.sampleProvider.fetchSample();
			curvedLineFollower.sampleRGBValue[0] = curvedLineFollower.sampleRGBValue[0]*256f;
			curvedLineFollower.sampleRGBValue[1] = curvedLineFollower.sampleRGBValue[1]*256f;
			curvedLineFollower.sampleRGBValue[2] = curvedLineFollower.sampleRGBValue[2]*256f;
			
			// Get the name of the detected RGB value
			String detectedColor = Color.getColor(curvedLineFollower.sampleRGBValue, toAvoidColor, toFollowColor);
			
			// Print the detected color
			LCD.clear();
			LCD.drawString(detectedColor, 0, 1);
			
			if(detectedColor.equals(toFollowColor.getName())) {
				if(timeInBlack == 0)
					timeInBlack=System.currentTimeMillis();
				else if((System.currentTimeMillis() - timeInBlack) > 500) {
					curvedLineFollower.motorSpeed = 400;
					angle=0.3f;
				}else if((System.currentTimeMillis() - timeInBlack) > 200) {
					curvedLineFollower.motorSpeed = 350;
					angle=0.35f;
				}else {
					curvedLineFollower.motorSpeed = 300;
					angle=0.4f;
				}
				timeInWhite = 0;
				curvedLineFollower.largeRegulatedMotorGauche.setSpeed(curvedLineFollower.motorSpeed);
				curvedLineFollower.largeRegulatedMotorDroit.setSpeed((float) angle * curvedLineFollower.motorSpeed);
			}
			else {
				if(timeInWhite == 0)
					
					timeInWhite = System.currentTimeMillis();
				else if((System.currentTimeMillis() - timeInWhite) > 1900 && timeInWhite != 0 ) {
					curvedLineFollower.largeRegulatedMotorDroit.stop();
					curvedLineFollower.largeRegulatedMotorDroit.stop();
					curvedLineFollower.lookForLine();
					timeInWhite = 0;
				}
				timeInBlack = 0;
				
				curvedLineFollower.largeRegulatedMotorGauche.setSpeed((float) angle * curvedLineFollower.motorSpeed);
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
