package T07;

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class DPM {
	
	static private final double LEFT_RADIUS = 12;
	static private final double RIGHT_RADIUS = 12;
	static private final double WIDTH = 12;
	static private final NXTRegulatedMotor LEFTMOTOR = Motor.A;
	static private final NXTRegulatedMotor RIGHTMOTOR = Motor.B;
	static private final UltrasonicSensor LEFT_ULTRASONIC_SENSOR = new UltrasonicSensor(SensorPort.S3);
	static private final UltrasonicSensor RIGHT_ULTRASONIC_SENSOR = new UltrasonicSensor(SensorPort.S4);
	
	public static void main(String[] args){
		
		// Instantiate classes for basic components testing
		TwoWheeledRobot robot = new TwoWheeledRobot(LEFTMOTOR, RIGHTMOTOR, LEFT_ULTRASONIC_SENSOR, RIGHT_ULTRASONIC_SENSOR, LEFT_RADIUS, RIGHT_RADIUS, WIDTH);
		Odometer odo = new Odometer(robot);
		// Navigation navi = new Navigation(odo);
		
		int buttonChoice;
		RConsole.openBluetooth(100);
		RConsole.println("Connected!");
		do {
			LCD.clear();
			
			LCD.drawString(" Left  |  Right ", 0, 0);
			LCD.drawString("                ", 0, 1);
			LCD.drawString("       To       ", 0, 2);
			LCD.drawString("      start     ", 0, 3);
			LCD.drawString("     program    ", 0, 4);
			
			buttonChoice = Button.waitForAnyPress();
			
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		USPoller usPoller = new USPoller(robot);
		LCDInfo lcd = new LCDInfo(odo, usPoller);
		
		odo.timedOut();
		lcd.timedOut();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
	}

}
