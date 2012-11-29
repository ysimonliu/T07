package T07;


import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import bluetooth.*;
/**
 * Main class of the entire project.
 * Contains all information for proper functioning of the master brick.
 *
 */
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
	private static final double tileLength = 30.48;
	
	/**
	 * Main method of the robot that controls all master slave functionality.
	 * This main method retrieves bluetooth information that determines role and start corner.
	 * Depending on role the method will have 2 different functions, one will handle grabbing and hiding the flag
	 * while the other will handle finding and placing the flag.
	 * 
	 * @param args
	 */
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
		Navigation navigation = new Navigation(robot, odometer, usPoller, lp1, lp2);
		MidLightSensorController midLightSensor = new MidLightSensorController(robot);
		Hider hider = new Hider(odometer, navigation, usPoller, midLightSensor);

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
		
		// starts either the attacker or defender code based on the bluetooth command passed
		switch(role) {
		case ATTACKER:{
			
			// gets the destination point for the flag
			int dropX = btReceiver.getDx();
			int dropY = btReceiver.getDy();

			// start the LCD display
			startLCDDisplay(odometer, usPoller, communicationController, lp2, lp2);
			
			// localize
			localize(odometer, navigation, usPoller, lp1, lp2, corner);
			
			// search
			Searcher search = new Searcher(odometer, navigation, usPoller, midLightSensor, hider);
			search.findBeacon();
			
			// flaghandler (pickup)
			hider.positionAndGrab();
			
			// navigate to drop off point
			navigation.travelTo(dropX*tileLength, dropY*tileLength, true);
			
			// flaghandler (drop)
			hider.putDownAndGo();
			
			// navigate to end point (corner)
			navigation.travelTo(-15, -15, false);
			
			break;
			}
		// Defender role	
		case DEFENDER:{
			
			// gets the flag point
			int flagX = btReceiver.getFx();
			int flagY = btReceiver.getFy();

			// start the LCD display
			startLCDDisplay(odometer, usPoller, communicationController, lp2, lp2);
			
			// localize
			localize(odometer, navigation, usPoller, lp1, lp2, corner);
			
			// hider
			hider.pickUpDefender(flagX, flagY);
			hider.hide();
			// navigate to end point (corner)
			navigation.travelTo(-15, -15, false);
			break;
			}
		}
		
		
		// once the escape button is pressed, the robot will exit
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	/**
	 * Method starts the LCD, created to contain all LCD operations.
	 * 
	 * @param odo Odometer object that needs to be displayed to brick screen
	 * @param usPoller USPoller object that allows for sensor readings to be displayed, used for debugging
	 * @param communicationController
	 * @param lp1 LightPoller object that allows for sensor readings to be displayed, used for debugging
	 * @param lp2 LightPoller object that allows for sensor readings to be displayed, used for debugging
	 */
	// method that sets the LCD screen up
	private static void startLCDDisplay(Odometer odo, USPoller usPoller, CommunicationController communicationController, LightPoller lp1, LightPoller lp2) {
		LCDInfo lcd = new LCDInfo(odo, usPoller, communicationController, lp1, lp2);
		lcd.timedOut();
	}
	
	/**
	 * Method that contains all localization subroutines, called to orientate the robot correctly.
	 * Requires a corner parameter for correct localization relative to the field.
	 * 
	 * @param odo Odometer object that is used by the USLocalizer and LightLocalizer
	 * @param navi Navigation object that is used by the USLocalizer and LightLocalizer
	 * @param usPoller USPoller object that will be used in ultrasonic localization
	 * @param lp1 left LightPoller that is used in the light localization
	 * @param lp2 right LightPoller that is used in the light localization
	 * @param corner int that will specify the starting corner
	 */
	private static void localize(Odometer odo, Navigation navi, USPoller usPoller, LightPoller lp1, LightPoller lp2, int corner){
		USLocalizer usLocalizer = new USLocalizer(odo, navi, usPoller);		
		LightLocalizer lightLocalizer = new LightLocalizer(odo, lp1, lp2, navi); 
		// performs us and light localization subroutines
		usLocalizer.doLocalization();
		lightLocalizer.doLocalization(corner);
		Sound.twoBeeps();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that will display a menu before starting the program.
	 * Used primarily for debugging purposes.
	 */
	private static void menu() {
		int buttonChoice;
		do {
			LCD.clear();
			
			LCD.drawString(" Left  |  Right ", 0, 0);
			LCD.drawString("                ", 0, 1);
			LCD.drawString("       To       ", 0, 2);
			LCD.drawString("      start     ", 0, 3);
			LCD.drawString("     program    ", 0, 4);
			
			// Displayed on the brick for debugging purposes, also prevents premature start of the program
			buttonChoice = Button.waitForAnyPress();
			
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
	}
}
