package motorMove;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class MotorMove
{
	public static void main(String[] args)
	{
		// Get the motors left and right
		EV3LargeRegulatedMotor largeRegulatedMotorGauche = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor largeRegulatedMotorDroit = new EV3LargeRegulatedMotor(MotorPort.D);
		
		// Set the speed of the motors
		largeRegulatedMotorGauche.setSpeed(400);
		largeRegulatedMotorDroit.setSpeed(400);
		
		// Make the motors move forward
		largeRegulatedMotorGauche.forward();
		largeRegulatedMotorDroit.forward();
		
		// While the Enter button is not clicked
		while(Button.ENTER.isUp())
		{
			// Wait for a button to be pressed
			int buttonPressed = Button.waitForAnyPress();
			
			// If its the UP button
			if(buttonPressed == Button.ID_UP)
			{
				int getActualSpeedGauche = largeRegulatedMotorGauche.getSpeed();
				
				// Add 100 to the speed of the left motor 
				largeRegulatedMotorGauche.setSpeed(getActualSpeedGauche + 100);
			}
			
			// If its the DOWN button
			if(buttonPressed == Button.ID_DOWN)
			{
				int getActualSpeedGauche = largeRegulatedMotorGauche.getSpeed();
				
				// Sub 100 from the speed of the left motor
				largeRegulatedMotorGauche.setSpeed(getActualSpeedGauche - 100);
			}
			
			// If its the LEFT button
			if(buttonPressed == Button.ID_LEFT)
			{
				int getActualSpeedDroit = largeRegulatedMotorDroit.getSpeed();
				
				// Sub 100 from the speed of the right motor
				largeRegulatedMotorDroit.setSpeed(getActualSpeedDroit - 100);
			}
			
			// If its the RIGHT button
			
			if(buttonPressed == Button.ID_RIGHT)
			{
				int getActualSpeedDroit = largeRegulatedMotorDroit.getSpeed();
				
				// Add 100 to the speed of the right motor
				largeRegulatedMotorDroit.setSpeed(getActualSpeedDroit + 100);
			}
		}
		
		// Stop the motors
		largeRegulatedMotorGauche.close();
		largeRegulatedMotorDroit.close();
	}
}
