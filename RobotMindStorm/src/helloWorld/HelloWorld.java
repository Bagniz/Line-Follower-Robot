package helloWorld;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class HelloWorld 
{
	public static void main(String[] args)
	{
		// Print 'Hello World!!!' on the screen
		LCD.drawString("Hello World!!!", 0, 2);
		// Wait for 10 seconds before stopping the program
		Delay.msDelay(10000);
	}
}
