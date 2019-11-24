package motorMove;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class MotorMove
{
	public static void main(String[] args)
	{
		EV3LargeRegulatedMotor largeRegulatedMotorGauche = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor largeRegulatedMotorDroit = new EV3LargeRegulatedMotor(MotorPort.D);
		
		largeRegulatedMotorGauche.setSpeed(400);
		largeRegulatedMotorDroit.setSpeed(400);
		
		largeRegulatedMotorGauche.forward();
		largeRegulatedMotorDroit.forward();
		
		while(Button.ENTER.isUp())
		{
			int buttonPressed = Button.waitForAnyPress();
			
			if(buttonPressed == Button.ID_UP)
			{
				int getActualSpeedGauche = largeRegulatedMotorGauche.getSpeed();
				
				largeRegulatedMotorGauche.setSpeed(getActualSpeedGauche + 100);
			}
			
			if(buttonPressed == Button.ID_DOWN)
			{
				int getActualSpeedGauche = largeRegulatedMotorGauche.getSpeed();
				
				largeRegulatedMotorGauche.setSpeed(getActualSpeedGauche - 100);
			}
			
			if(buttonPressed == Button.ID_LEFT)
			{
				int getActualSpeedDroit = largeRegulatedMotorDroit.getSpeed();
				
				largeRegulatedMotorDroit.setSpeed(getActualSpeedDroit - 100);
			}
			
			if(buttonPressed == Button.ID_RIGHT)
			{
				int getActualSpeedDroit = largeRegulatedMotorDroit.getSpeed();
				
				largeRegulatedMotorDroit.setSpeed(getActualSpeedDroit + 100);
			}
		}
		
		largeRegulatedMotorGauche.close();
		largeRegulatedMotorDroit.close();
	}
}
