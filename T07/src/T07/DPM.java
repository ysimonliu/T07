package T07;


import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import bluetooth.*;

public class DPM {
	
	public enum USSensor {MIDDLE, RIGHT};
	public enum LSensor {LEFT, RIGHT};
	static private final NXTRegulatedMotor LEFT_MOTOR = Motor.A; 
	static private final NXTRegulatedMotor RIGHT_MOTOR = Motor.B;
	static private final NXTRegulatedMotor LIGHT_SENSOR_MOTOR = Motor.C;
	static private final LightSensor LEFT_LIGHT_SENSOR = new LightSensor(SensorPort.S1);
	static private final LightSensor RIGHT_LIGHT_SENSOR = new LightSensor(SensorPort.S2);
	static private final UltrasonicSensor MIDDLE_ULTRASONIC_SENSOR = new UltrasonicSensor(SensorPort.S3);
	private static StartCorner startCorner; // to determine the start corner of the robot
	private static PlayerRole role;
	private static int corner = 1;
	private static int intRole; // 0 means attacker, 1 means defender
	
	public static void main(String[] args){
		
		// get ready to connect to slave brick
		CommunicationServer communicationServer = new CommunicationServer();
		// establish connection
		CommunicationController communicationController = new CommunicationController(communicationServer);
		// Instantiate classes of all basic components
		TwoWheeledRobot robot = new TwoWheeledRobot(LEFT_MOTOR, RIGHT_MOTOR, LIGHT_SENSOR_MOTOR, MIDDLE_ULTRASONIC_SENSOR, LEFT_LIGHT_SENSOR, RIGHT_LIGHT_SENSOR, communicationController);
		Odometer odometer = new Odometer(robot);
		USPoller usPoller = new USPoller(robot);
		LightPoller lp1 = new LightPoller(robot, LSensor.LEFT);
		LightPoller lp2 = new LightPoller(robot, LSensor.RIGHT);
		Navigation2 navigation = new Navigation2(odometer, usPoller, lp1, lp2);
		MidLightSensorController midLightSensor = new MidLightSensorController(robot);
		// connect to bluetooth for debug
		//RConsole.openBluetooth(20000);
		//Sound.twoBeeps();
		//RConsole.println("Connected!");
		/*
		//start to get connection with Bluetooth server provided by TA
		BTReceiver btReceiver = new BTReceiver();
		startCorner = btReceiver.getCorner(); // this gets the start corner for use by the searcher and the localizer
		role = btReceiver.getRole();

		// switch that gets the starting corner and sets it as an int, for use by the 
		switch(startCorner) {
		case BOTTOM_LEFT:
			corner = 1;
			break;
		case TOP_LEFT:
			corner = 2;
			break;
		case TOP_RIGHT:
			corner = 3;
			break;
		case BOTTOM_RIGHT:
			corner = 4;
			break;
		}
		
		switch(role) {
		case ATTACKER:
			intRole = 0;
			break;
		case DEFENDER:
			intRole = 1;
			break;
		}
		
		// Attacker role
		if (intRole == 0) {
			
			// gets the destination point for the flag
			int dropX = btReceiver.getDx();
			int dropY = btReceiver.getDy();
			
			// main menu
			menu();	

			// start the LCD display
			startLCDDisplay(odometer, usPoller, communicationController, lp2, lp2);
			
			// localize
			localize(odometer, navigation, usPoller, lp1, lp2, corner);
			
			// search
			Searcher search = new Searcher(odometer, navigation, usPoller, midLightSensor);
			search.findBeacon(corner);
			
			// flaghandler (pickup)
			
			// navigate to drop off point
			navigation.travelTo(dropX, dropY, true);
			
			// flaghandler (drop)
			
			// navigate to end point (corner)
			navigation.travelTo(0, 0, true);
			
		// Defender role	
		} else if (intRole == 1) {
			
			// gets the flag point
			int flagX = btReceiver.getFx();
			int flagY = btReceiver.getFy();
			
			// main menu
			menu();	

			// start the LCD display
			startLCDDisplay(odometer, usPoller, communicationController, lp2, lp2);
			
			// localize
			localize(odometer, navigation, usPoller, lp1, lp2, corner);
			
			// navigate to flag
			navigation.travelTo(flagX, flagY, true);
			
			// flag handler (pickup)
			
			// hider
			
			// flag handler (drop)
			
			// navigate to end point
			navigation.travelTo(0, 0, true);
		}
		*/
		// main menu
		menu();	

		// start the LCD display
		startLCDDisplay(odometer, usPoller, communicationController, lp2, lp2);
		
		Searcher search = new Searcher(odometer, navigation, usPoller, midLightSensor);
		search.findBeacon(1);
		search.positionAndGrabBeacon();
		//OdometryCorrection correct = new OdometryCorrection (odometer, lp1, lp2);
		//correct.start();
		
		// localize
		//localize(odometer, navigation, usPoller, lp1, lp2);

		/*
		navigation.travelTo(60.96, 60.96, true);
		navigation.travelTo(0, 0, true);
		navigation.travelTo(60.96, 0, true);
		navigation.travelTo(60.96, 60.96, true);
		navigation.travelTo(0, 0, true);
		*/
		
		
		// once the escape button is pressed, the robot will exit
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
	private static void startLCDDisplay(Odometer odo, USPoller usPoller, CommunicationController communicationController, LightPoller lp1, LightPoller lp2) {
		LCDInfo lcd = new LCDInfo(odo, usPoller, communicationController, lp1, lp2);
		lcd.timedOut();
	}

	private static void localize(Odometer odo, Navigation2 navi, USPoller usPoller, LightPoller lp1, LightPoller lp2, int corner){
		USLocalizer usLocalizer = new USLocalizer(odo, navi, usPoller);		
		LightLocalizer lightLocalizer = new LightLocalizer(odo, lp1, lp2, navi); 
		// performs us and light localization subroutines
		usLocalizer.doLocalization();
		lightLocalizer.doLocalization(corner);
	}
	
	private static void menu() {
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
	}
}
