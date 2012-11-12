// EVENTUALLY NEED TO COMBINE BOTH LIGHTLOCALIZER AND USLOCALIZER INTO ONE

package T07;

import lejos.nxt.*;


public class LightLocalizer{
	// initialize the odometer, twoWheeledRobot, lightSensor and Navigation
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightPoller leftLight;
	private LightPoller rightLight;
	private Navigation navigation;
	// distance from light sensor to the center of the axle
	private double lightSensorD = 13.5;
	private double[]angles = new double[4];
	public static double ROTATION_SPEED = 20, FORWARD_SPEED = 10;
	// the intensity of the grid line on the tile, to avoid using magic numbers
	private static int gridLineIntensity = 475;
	
	// constructor
	public LightLocalizer(Odometer odo, LightPoller leftLight, LightPoller rightLight, Navigation navi) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.leftLight = leftLight;
		this.rightLight = rightLight;
		this.navigation = navi;
	}
	
	// Method that controls light localization
	public void doLocalization() {
		
		// Moves the robot forward towards first gridline
		robot.setForwardSpeed(30); //TODO check the robot speed
		
		// This loop detects the first line (the x axis) and stops directly on it
		while(robot.leftMotorMoving() || robot.rightMotorMoving()) {
			
			if (leftLight.getRawData() < gridLineIntensity) { // TODO check the light value reading, will it detect line
				robot.stopLeftMotor();
				Sound.beep();
			}
			if (rightLight.getRawData() < gridLineIntensity) { // TODO check the light value reading, will it detect line
				robot.stopRightMotor();
				Sound.beep();
			}
		}
		
		// COMMENTED OUT TO TEST... PROBLEM WITH LIGHT SENSORS CONTACT ASHLEY
		/*
		// sets the robot to 90 degrees so that the turn to method moves to 0 degrees correctly
		double data1[] = {0, 0, 0};
		boolean pos1[] = {false, false, true};	
		odo.setPosition(data1, pos1);
		
		navigation.turnTo(90); // Will turn the robot to 0 degrees assuming that pos X is 0 degrees, TODO check convention
		
		// Moves the robot forward towards second gridline
		robot.setForwardSpeed(30); // TODO check the robot speed
		
		// This loop detects the second line (the y axis) and stops directly on it
		while(robot.leftMotorMoving() || robot.rightMotorMoving()) {
			if (leftLight.getFilteredData() > 480) { // TODO check the light value reading
				robot.stopLeftMotor();
			}
			if (rightLight.getFilteredData() > 480) { // TODO check the light value reading
				robot.stopRightMotor();
			}
		}
		
		// finally sets the localized positions of the robot
		double data2[] = {0, 0, 90};
		boolean pos2[] = {true, true, true};
		odo.setPosition(data2, pos2);		
		*/
	}
}