package T07;

import lejos.nxt.*;

/**
 * Class that is responsible for all ultrasonic localization
 * Accurate to about 10 degrees
 * 
 * @author Ashley Simpson
 */
public class USLocalizer{
	// declare falling_edge, and rising_edge
	public enum LocalizationType {FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 50;
	// noWallDistance is a safe distance to make sure that robot sees no wall, wall distance is the threshold for seeing a wall
	private double wallDistance = 45, noWallDistance = 45;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private USPoller usPoller;
	private double headingDiff;
	private Navigation navigation;

	/**
	 * Constructor for the USLocalizer
	 * 
	 * @param odo Odometer object for odometer heading updates and TwoWheeledRobot passing
	 * @param navigation Navigation object for rotations
	 * @param usPoller USPoller object for wall detection
	 */
	public USLocalizer(Odometer odo, Navigation navigation,USPoller usPoller) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.navigation = navigation;
		this.usPoller = usPoller;
	}
	
	/**
	 * Method that performs all tasks for ultrasonic localization of the robot.
	 * Primarily for accurate placement so light localization can occur.
	 */
	public void doLocalization() {
		double[] pos = new double [3];
		double angleA, angleB;

		// if facing the wall, rotate until the robot sees no wall
		robot.setRotationSpeed(ROTATION_SPEED);
		while(usPoller.getFilteredData() < wallDistance){			
		}
		// rotate the robot until it sees a wall
		while(usPoller.getFilteredData() > noWallDistance){
		}
		robot.stop();
		
		// stores the odometer heading, to be used in calculating actual heading
		odo.getPosition(pos);
		angleA = pos[2];
		Sound.beep(); // indicate that robot has detected a wall
		
		robot.setRotationSpeed(-ROTATION_SPEED);
		// switch direction and wait until it sees no wall
		while(usPoller.getFilteredData() < wallDistance) {
		}
		// keep rotating until the robot sees a wall, then latch the angle+
		while(usPoller.getFilteredData() > noWallDistance) {
		}
		
		robot.stop();
		
		// stores the odometer heading, to be used in calculating actual heading
		odo.getPosition(pos);
		angleB = pos[2];
		Sound.beep();

		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'
		if (angleA > angleB) {
			headingDiff = 45 - (angleA + angleB)/2; 
		}
		else {
			headingDiff = 225 - (angleA + angleB)/2;
		}
		
		// update the odometer position (example to follow:)
		//odo.getPosition(pos);
		odo.setPosition(new double [] {0.0, 0.0, pos[2] + headingDiff}, new boolean [] {true, true, true});
		// turn to the true "north", which is the 0 degree after correction
		navigation.turnTo(0);
	}

}