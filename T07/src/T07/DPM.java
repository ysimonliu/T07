package T07;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.*;

public class DPM {
	
	static private final double LEFT_RADIUS = 12;
	static private final double RIGHT_RADIUS = 12;
	static private final double WIDTH = 12;
	static private final NXTRegulatedMotor LEFTMOTOR = Motor.A;
	static private final NXTRegulatedMotor RIGHTMOTOR = Motor.B;
	
	public static void main(String[] args){
		// This code now is to test the RConsole, commented out to test localization
		//RConsole.openBluetooth(20000);
		//RConsole.println("Connected!");
		//printNums();
		
		//Basic parameters for testing different classes (Ashley)
		int buttonChoice;
		
		do {
			LCD.clear();
			
			LCD.drawString(" Left  |  Right ", 0, 0);
			LCD.drawString("                ", 0, 1);
			LCD.drawString("       To       ", 0, 2);
			LCD.drawString("      start     ", 0, 3);
			LCD.drawString("     program    ", 0, 4);
			
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		// Setting up the sensors
		//LightSensor lsl = new LightSensor(SensorPort.S1); // left lightsensor
		//LightSensor lsr = new LightSensor(SensorPort.S2); // right lightsensor
		UltrasonicSensor usl = new UltrasonicSensor(SensorPort.S3); // left ultrasonicsensor
		UltrasonicSensor usr = new UltrasonicSensor(SensorPort.S4); // right ultrasonicsensor
		
		// Setting up the basic classes for localization
		TwoWheeledRobot robo = new TwoWheeledRobot(LEFTMOTOR, RIGHTMOTOR, LEFT_RADIUS, RIGHT_RADIUS, WIDTH);
		Odometer odo = new Odometer(robo);
		Navigation navi = new Navigation(odo);	
		LCDInfo lcd = new LCDInfo(odo);
		USPoller leftUS = new USPoller(usl); // Poller for the left ultrasonic sensor
		USPoller rightUS = new USPoller(usr); // Poller for the right ultrasonic sensor
		
		odo.timedOut();
		USLocalizer uSLOC = new USLocalizer(odo, leftUS, rightUS, USLocalizer.LocalizationType.FALLING_EDGE, navi);
		uSLOC.doLocalization();	
			
	}
	
	/*
	private static void printNums() {
		int i;
		for (i = 0; i < 10; i++) {
			RConsole.println("The value of i is " + String.valueOf(i));
		}
	}
	*/
}
