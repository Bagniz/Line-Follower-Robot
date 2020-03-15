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
	float[] sampleRGBValue;
	int motorSpeed;
	RemoteEV3 ev3;
	RMISampleProvider sampleProvider;
	EV3LargeRegulatedMotor motorLeft, motorRight;
	static ColorCustom toAvoidColor, toFollowColor;
	
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
	
	// Look for the line when lost
	public void lookForLine(CurvedLineFollowerCustom curvedLineFollower) throws RemoteException {
		// Variables
		String detectedColorName;
		long timeTurning = 0;
		int timeToSpeedUp = 3000;
		float turnPercentAvoid = 0.50f;
		
		while(Button.ESCAPE.isUp()) {
			
			// Detect a color and get the RGB values
			curvedLineFollower.sampleRGBValue = curvedLineFollower.sampleProvider.fetchSample();
			curvedLineFollower.sampleRGBValue[0] = curvedLineFollower.sampleRGBValue[0]*256f;
			curvedLineFollower.sampleRGBValue[1] = curvedLineFollower.sampleRGBValue[1]*256f;
			curvedLineFollower.sampleRGBValue[2] = curvedLineFollower.sampleRGBValue[2]*256f;
			
			// Calculate the distances
			detectedColorName = ColorCustom.getColor(curvedLineFollower.sampleRGBValue, toAvoidColor, toFollowColor);
			
			// Is it color to Avoid
			if(detectedColorName.equals(ColorCustom.COLOR_AVOID)) {
				if(timeTurning == 0)
					timeTurning = System.currentTimeMillis();
				motorLeft.setSpeed((float)(turnPercentAvoid * motorSpeed));
				motorRight.setSpeed(motorSpeed);
				
				// Make the circle radius bigger
				if(System.currentTimeMillis() - timeTurning > timeToSpeedUp) {
					timeTurning = 0;
					timeToSpeedUp += 2000;
					turnPercentAvoid += 0.10f;
				}
			}
			// Is it color to Follow
			else {
				curvedLineFollower.motorLeft.setSpeed(motorSpeed);
				curvedLineFollower.motorLeft.forward();
				curvedLineFollower.motorRight.setSpeed(motorSpeed);
				curvedLineFollower.motorRight.backward();
				Delay.msDelay(300);
				curvedLineFollower.motorLeft.stop(true);
				curvedLineFollower.motorRight.stop(true);
				return ;
			}
			
			// Make the motors move forward
			motorLeft.forward();
			motorRight.forward();
		}
	}

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		// Variables
		String detectedColorName;
		float turnAngle = 0.40f;
		long timeInAvoidColor = 0;
		
		// Initialize the brick
		CurvedLineFollowerCustom curvedLineFollower = new CurvedLineFollowerCustom();
		
		// Learn the color to follow
		ColorCustom.getColorTo(curvedLineFollower.sampleProvider, true);
		
		// Learn the color to avoid
		ColorCustom.getColorTo(curvedLineFollower.sampleProvider, false);
		
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
			
			// Calculate the distances
			detectedColorName = ColorCustom.getColor(curvedLineFollower.sampleRGBValue, toAvoidColor, toFollowColor);
			
			// Is it color to follow
			if(detectedColorName.equals(ColorCustom.COLOR_FOLLOW)) {
				timeInAvoidColor = 0;
				curvedLineFollower.motorLeft.setSpeed(curvedLineFollower.motorSpeed);
				curvedLineFollower.motorRight.setSpeed((float)(turnAngle * curvedLineFollower.motorSpeed));
			}
			// Is it color to avoid
			else {
				if(timeInAvoidColor == 0)
					// Start calculating time in avoid color
					timeInAvoidColor = System.currentTimeMillis();
				
				curvedLineFollower.motorLeft.setSpeed((float)(turnAngle * curvedLineFollower.motorSpeed));
				curvedLineFollower.motorRight.setSpeed(curvedLineFollower.motorSpeed);
				
				// If time is big than we search for line
				// The machine is lost
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
