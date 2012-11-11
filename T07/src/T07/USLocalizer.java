// EVENTUALLY NEED TO COMBINE BOTH LIGHTLOCALIZER AND USLOCALIZER INTO ONE

package T07;

import lejos.nxt.*;

public class USLocalizer{
	// declare falling_edge, and rising_edge
	public enum LocalizationType {FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
	// noWallDistance is a safe distance to make sure that robot sees no wall, wall distance is the threshold for seeing a wall
	private double wallDistance = 40, noWallDistance = 50;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private USPoller usLeft;
	private USPoller usRight;
	private LocalizationType locType;
	private double headingDiff;
	//private int filterCounter, lastNormalReading; // values for the getdistance method that isn't required
	private Navigation navigation;

	// constructor
	public USLocalizer(Odometer odo, LocalizationType locType, Navigation navigation) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.locType = locType;
		this.navigation = navigation;
	}

	public void doLocalization() {
		double[] pos = new double [3];
		double angleA, angleB;

		// if choosing falling edge
		if (locType.equals(LocalizationType.FALLING_EDGE)) {

			// if facing the wall, rotate until the robot sees no wall
			while(usRight.getFilteredData() < noWallDistance){
				robot.setRotationSpeed(ROTATION_SPEED);
			}

			// rotate the robot until it sees no wall
			while(usRight.getFilteredData() > wallDistance){
				robot.setRotationSpeed(ROTATION_SPEED);
			}
			robot.setRotationSpeed(0);

			// keep rotating until the robot sees a wall, then latch the angle
			odo.getPosition(pos);
			angleA = pos[2];
			Sound.beep();

			// switch direction and wait until it sees no wall
			while(usLeft.getFilteredData() <= noWallDistance) {
				robot.setRotationSpeed(-ROTATION_SPEED);
			}

			// keep rotating until the robot sees a wall, then latch the angle+
			while(usLeft.getFilteredData() > wallDistance) {
				robot.setRotationSpeed(-ROTATION_SPEED);
			}
			robot.setRotationSpeed(0);
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
			odo.getPosition(pos);
			odo.setPosition(new double [] {0.0, 0.0, pos[2] + headingDiff}, new boolean [] {true, true, true});
			// turn to the true "north", which is the 0 degree after correction
			navigation.turnTo(0);
		}
		// if choosing rising edge
		else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */

			// if first facing away from a wall, then first turn to a wall
			while(usRight.getFilteredData() > wallDistance) {
				robot.setRotationSpeed(ROTATION_SPEED);
			}
			// rotate the robot until it sees no wall
			while(usRight.getFilteredData() < wallDistance){
				robot.setRotationSpeed(ROTATION_SPEED);
			}
			robot.setRotationSpeed(0);

			// keep rotating until the robot just sees no wall, then latch the angle
			odo.getPosition(pos);
			angleA = pos[2];
			Sound.beep();

			// switch direction and wait until it sees a wall
			while(usLeft.getFilteredData() > wallDistance) {
				robot.setRotationSpeed(-ROTATION_SPEED);
			}

			// keep rotating until the robot just doesn't see a wall, then latch the angle+
			while(usLeft.getFilteredData() < wallDistance) {
				robot.setRotationSpeed(-ROTATION_SPEED);
			}
			robot.setRotationSpeed(0);
			odo.getPosition(pos);
			angleB = pos[2];
			Sound.beep();

			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			if (angleA < angleB) {
				headingDiff = 45 - (angleA + angleB)/2; 
			}
			else {
				headingDiff = 225 - (angleA + angleB)/2;
			}
			// update the odometer position (example to follow:)
			odo.getPosition(pos);
			odo.setPosition(new double [] {0.0, 0.0, pos[2] + headingDiff}, new boolean [] {true, true, true});
			// turn to the true "north", which is the 0 degree after correction
			navigation.turnTo(0);

		}
	}

}