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
		
		largeRegulatedMotorGauche.forward();
		largeRegulatedMotorDroit.forward();
		
		while(Button.ENTER.isUp())
		{
			if(Button.UP.isDown())
			{
				int getActualSpeedGauche = largeRegulatedMotorGauche.getSpeed();
				int getActualSpeedDroit = largeRegulatedMotorDroit.getSpeed();
				
				largeRegulatedMotorDroit.setSpeed(getActualSpeedDroit + 100);
				largeRegulatedMotorGauche.setSpeed(getActualSpeedGauche + 100);
			}
			
			if(Button.DOWN.isDown())
			{
				int getActualSpeedGauche = largeRegulatedMotorGauche.getSpeed();
				int getActualSpeedDroit = largeRegulatedMotorDroit.getSpeed();
				
				largeRegulatedMotorDroit.setSpeed(getActualSpeedDroit - 100);
				largeRegulatedMotorGauche.setSpeed(getActualSpeedGauche - 100);
			}
		}
		
		largeRegulatedMotorGauche.close();
		largeRegulatedMotorDroit.close();
	}
}
