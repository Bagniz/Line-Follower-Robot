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

public class CurvedLineFollowerCustom {
	
	// Attributes
	Color colorToFollow;
	Color colorToAvoid;
	float[] sampleRGBValue;
	int motorSpeed;
	RemoteEV3 ev3;
	RMISampleProvider sampleProvider;
	EV3LargeRegulatedMotor motorLeft, motorRight;
	
	// Constructor
	public CurvedLineFollowerCustom() throws RemoteException, MalformedURLException, NotBoundException {
		// RGB sample variable
		this.sampleRGBValue = new float[3];
		
		// Get the connected EV3Brick and Color Sensor
		this.ev3 = new RemoteEV3("10.0.1.1");
		this.sampleProvider = ev3.createSampleProvider("S4", "lejos.hardware.sensor.EV3ColorSensor", "RGB");
		
		// Get the motors left and right
		this.motorLeft = new EV3LargeRegulatedMotor(MotorPort.D);
		this.motorRight = new EV3LargeRegulatedMotor(MotorPort.A);
		
		// Set the speed of the motors
		motorSpeed = 500;
		motorLeft.setSpeed(motorSpeed);
		motorRight.setSpeed(motorSpeed);
	}
	
	boolean getColorToFollow() throws RemoteException {
		// Variables
		int buttonClicked;
		
		LCD.clear();
		LCD.drawString("Color to follow", 0, 1);
		
		while(true) {
			// Wait for the button press
			buttonClicked = Button.waitForAnyPress();
			
			// Is it the Enter button
			if(buttonClicked == Button.ID_ENTER) {
				// Detect a color and get the RGB values
				this.sampleRGBValue = this.sampleProvider.fetchSample();
				this.sampleRGBValue[0] = this.sampleRGBValue[0] * 256f;
				this.sampleRGBValue[1] = this.sampleRGBValue[1] * 256f;
				this.sampleRGBValue[2] = this.sampleRGBValue[2] * 256f;
				
				// Create the color to follow
				this.colorToFollow = new Color("ColorFollow", this.sampleRGBValue);
				LCD.drawString("Color to follow detected", 0, 2);
				
				return true;
			}
			// Is it the escape button
			else if(buttonClicked == Button.ID_ESCAPE) {
				break;
			}	
		}
		return false;
	}
	
	boolean getColorToAvoid() throws RemoteException {
		// Variables
		int buttonClicked;
		
		LCD.clear();
		LCD.drawString("Color to avoid", 0, 1);
		
		while(true) {
			// Wait for the button press
			buttonClicked = Button.waitForAnyPress();
			
			// Is it the Enter button
			if(buttonClicked == Button.ID_ENTER) {
				// Detect a color and get the RGB values
				this.sampleRGBValue = this.sampleProvider.fetchSample();
				this.sampleRGBValue[0] = this.sampleRGBValue[0] * 256f;
				this.sampleRGBValue[1] = this.sampleRGBValue[1] * 256f;
				this.sampleRGBValue[2] = this.sampleRGBValue[2] * 256f;
				
				// Create the color to follow
				this.colorToAvoid = new Color("ColorAvoid", this.sampleRGBValue);
				LCD.drawString("Color to avoid detected", 0, 2);
				
				return true;
			}
			// Is it the escape button
			else if(buttonClicked == Button.ID_ESCAPE) {
				break;
			}	
		}
		return false;
	}
	
	void lookForLine(CurvedLineFollowerCustom curvedLineFollower) throws RemoteException {
		// Variables
		long timeTurning = 0;
		int timeToSpeeUp = 3000;
		double ditanceToFollow = 0, distanceToAvoid = 0;
		float turnPercentWhite = 0.50f;
		
		// Detect a color and get the RGB values
		sampleRGBValue = sampleProvider.fetchSample();
		sampleRGBValue[0] = sampleRGBValue[0]*256f;
		sampleRGBValue[1] = sampleRGBValue[1]*256f;
		sampleRGBValue[2] = sampleRGBValue[2]*256f;
		
		// Calculate the distance
		ditanceToFollow = Color.getDistance(sampleRGBValue, colorToFollow.getRgbColorValues());
		distanceToAvoid = Color.getDistance(sampleRGBValue, colorToAvoid.getRgbColorValues());
		
		while(Button.ESCAPE.isUp()) {
			// Detect a color and get the RGB values
			curvedLineFollower.sampleRGBValue = curvedLineFollower.sampleProvider.fetchSample();
			curvedLineFollower.sampleRGBValue[0] = curvedLineFollower.sampleRGBValue[0]*256f;
			curvedLineFollower.sampleRGBValue[1] = curvedLineFollower.sampleRGBValue[1]*256f;
			curvedLineFollower.sampleRGBValue[2] = curvedLineFollower.sampleRGBValue[2]*256f;
			
			// Calculate the distance
			ditanceToFollow = Color.getDistance(curvedLineFollower.sampleRGBValue, curvedLineFollower.colorToFollow.getRgbColorValues());
			distanceToAvoid = Color.getDistance(curvedLineFollower.sampleRGBValue, curvedLineFollower.colorToAvoid.getRgbColorValues());
			
			// Is is follow turn left
			if(ditanceToFollow > distanceToAvoid) {
				if(timeTurning == 0)
					timeTurning = System.currentTimeMillis();
				motorLeft.setSpeed((float)(turnPercentWhite * motorSpeed));
				motorRight.setSpeed(motorSpeed);
				
				if(System.currentTimeMillis() - timeTurning > timeToSpeeUp) {
					timeTurning = 0;
					timeToSpeeUp += 2000;
					turnPercentWhite += 0.10f;
				}
			}
			// It is avoid turn right
			else {
				return ;
			}
			
			// Make the motors move forward
			motorLeft.forward();
			motorRight.forward();
		}
	}

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		// Variables
		double ditanceToFollow = 0, distanceToAvoid = 0;
		float turnPercentBlack = 0.50f, turnPercentWhite = 0.50f;
		long timeInAvoidColor = 0;
		
		// Initialize the brick
		CurvedLineFollowerCustom curvedLineFollower = new CurvedLineFollowerCustom();
		
		// Detect the color to follow & color to avoid
		curvedLineFollower.getColorToFollow();
		curvedLineFollower.getColorToAvoid();
		
		// Start following the line 
		LCD.clear();
		LCD.drawString("Start following", 0, 1);
		Button.ENTER.waitForPressAndRelease();
		
		// While the brick is detecting
		while(Button.ESCAPE.isUp()) {
			// Detect a color and get the RGB values
			curvedLineFollower.sampleRGBValue = curvedLineFollower.sampleProvider.fetchSample();
			curvedLineFollower.sampleRGBValue[0] = curvedLineFollower.sampleRGBValue[0]*256f;
			curvedLineFollower.sampleRGBValue[1] = curvedLineFollower.sampleRGBValue[1]*256f;
			curvedLineFollower.sampleRGBValue[2] = curvedLineFollower.sampleRGBValue[2]*256f;
			
			// Calculate the distance
			ditanceToFollow = Color.getDistance(curvedLineFollower.sampleRGBValue, curvedLineFollower.colorToFollow.getRgbColorValues());
			distanceToAvoid = Color.getDistance(curvedLineFollower.sampleRGBValue, curvedLineFollower.colorToAvoid.getRgbColorValues());
			
			// Is is follow turn left
			if(ditanceToFollow < distanceToAvoid) {
				timeInAvoidColor = 0;
				curvedLineFollower.motorLeft.setSpeed(curvedLineFollower.motorSpeed);
				curvedLineFollower.motorRight.setSpeed((float)(turnPercentBlack * curvedLineFollower.motorSpeed));
			}
			// It is avoid turn right
			else {
				if(timeInAvoidColor == 0)
					// Start calculating time in avoid color
					timeInAvoidColor = System.currentTimeMillis();
				
				curvedLineFollower.motorLeft.setSpeed((float)(turnPercentWhite * curvedLineFollower.motorSpeed));
				curvedLineFollower.motorRight.setSpeed(curvedLineFollower.motorSpeed);
			
				// If time is big than we search for line
				if(System.currentTimeMillis() - timeInAvoidColor > 3000) {
					timeInAvoidColor = 0;
					curvedLineFollower.lookForLine(curvedLineFollower);
				}
			}
			
			// Make the motors move forward
			curvedLineFollower.motorLeft.forward();
			curvedLineFollower.motorRight.forward();
		}
		
		// Close brick resources
		curvedLineFollower.sampleProvider.close();
		curvedLineFollower.motorLeft.close();
		curvedLineFollower.motorRight.close();
	}

}
