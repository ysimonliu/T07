package T07;


import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
//import bluetooth.*; //TODO: some error here, fix when required

public class DPM {
	
	public enum USSensor {MIDDLE, RIGHT};
	public enum LSensor {LEFT, RIGHT};
	static private final NXTRegulatedMotor LEFT_MOTOR = Motor.A; // TODO: Will need to sort out the motor configurations with master/slave brick communications
	static private final NXTRegulatedMotor RIGHT_MOTOR = Motor.B;
	static private final LightSensor LEFT_LIGHT_SENSOR = new LightSensor(SensorPort.S1);
	static private final LightSensor RIGHT_LIGHT_SENSOR = new LightSensor(SensorPort.S2);
	static private final UltrasonicSensor MIDDLE_ULTRASONIC_SENSOR = new UltrasonicSensor(SensorPort.S3);
	
	public static void main(String[] args){
		
		// Instantiate classes for basic components testing
		TwoWheeledRobot robot = new TwoWheeledRobot(LEFT_MOTOR, RIGHT_MOTOR, MIDDLE_ULTRASONIC_SENSOR, LEFT_LIGHT_SENSOR, RIGHT_LIGHT_SENSOR);
		Odometer odo = new Odometer(robot);
		//Navigation navi = new Navigation(odo);
		//start to get connection with the client brick
		CommunicationServer communicationServer = new CommunicationServer();
		
		//start to get connection with Bluetooth server provided by TA
		//BTReceiver btReceiver = new BTReceiver();
		
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
		
		USPoller usPoller = new USPoller(robot);
		LightPoller lp1 = new LightPoller(robot, LSensor.LEFT);
		LightPoller lp2 = new LightPoller(robot, LSensor.RIGHT);
		
		//starting communication with the client brick, the communication controller is started in its constructor
		CommunicationController communicationController = new CommunicationController(communicationServer);
		
		LCDInfo lcd = new LCDInfo(odo, usPoller, communicationController, lp1, lp2);
		lcd.timedOut();
		
		// sets the basics for localization
		Navigation navi = new Navigation(odo, usPoller);	
		//USLocalizer usLocalizer = new USLocalizer(odo, navi, usPoller);		
		//LightLocalizer lightLocalizer = new LightLocalizer(odo, lp1, lp2, navi); 
		
		// performs us and light localization subroutines
		//usLocalizer.doLocalization();
		//lightLocalizer.doLocalization();
		
		// search algorithm, will search for the light source TODO: need to integrate with the second brick... HOW?
		//int x = 0; // TODO pass the x and y values to the searcher so it knows where the searching starts...
		//int y = 0;
		//Searcher search = new Searcher(odo, navi, lp3, usPoller, /*communicationController,*/ x, y);
		
		// performs the searching event
		//search.findBeacon();
		
		//FlagHandler flagHandler = new FlagHandler(odo, navi, usPoller);
		//flagHandler.pickUp();
		//try {
		//	Thread.sleep(1000);
		//} catch (InterruptedException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//flagHandler.putDown();
		
		// once the escape button is pressed, the robot will 
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
