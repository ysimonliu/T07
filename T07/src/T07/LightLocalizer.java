package T07;

import lejos.nxt.*;

/**
 * Class that performs all aspects of light localization.
 * Depending on the corner passed as the parameter the localization 
 * will be perfectly localized to that corner
 * 
 * @author Ashley Simpson
 */
public class LightLocalizer{
	// initialize the odometer, twoWheeledRobot, lightSensor and Navigation
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightPoller leftLight;
	private LightPoller rightLight;
	private Navigation navigation;
	public static double ROTATION_SPEED = 3, FORWARD_SPEED = 3;
	private static int gridLineIntensity = 50; // gridline intensity that is read by the light sensors
	private static final double tileLength = 30.48; // length of the tile for mathematics
	private double xCoordinate = 0;
	private double yCoordinate = 0;
	private int orientation = 0;
	
	/**
	 * Constructor for LightLocalizer
	 * 
	 * @param odo Odometer object that is used for localizing
	 * @param leftLight LightPoller that is used for localizing
	 * @param rightLight LightPoller that is used for localizing
	 * @param navi Navigation object that allows for rotating
	 */
	public LightLocalizer(Odometer odo, LightPoller leftLight, LightPoller rightLight, Navigation navi) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.leftLight = leftLight;
		this.rightLight = rightLight;
		this.navigation = navi;
	}
	
	/**
	 * Method that controls light localization, will perform all of the 
	 * movements required for perfect localization
	 * 
	 * @param corner int that is passed to determine final heading and coordinates
	 */
	public void doLocalization(int corner) {
		
		// depending on the starting corner the localization will be different
		// each corner has a specific case
		if (corner == 1) {
			xCoordinate = 0;
			yCoordinate = 0;
			orientation = 90;
		} else if (corner == 2) {
			xCoordinate = 0;
			yCoordinate = 10*tileLength;
			orientation = 180;
		} else if (corner == 3) {
			xCoordinate = 10*tileLength;
			yCoordinate = 10*tileLength;
			orientation = 270;
		} else if (corner == 4) {
			xCoordinate = 10*tileLength;
			yCoordinate = 0;
			orientation = 0;
		} // always an offset of 90 because of the localization movement
		
		// sets the robot heading towards the x-axis
		robot.setForwardSpeed(FORWARD_SPEED);
		
		// this will make the robot stop on the x axis correcting the y coordinates
		// will move each tire until it reaches a gridline, stopping directly on the line
		while (robot.leftMotorMoving() || robot.rightMotorMoving()) {
			if (leftLight.getSecondOrderDerivative() > gridLineIntensity) {
				Sound.beep();
				robot.stopLeftMotor();
			}
			if (rightLight.getSecondOrderDerivative() > gridLineIntensity) {
				Sound.beep();
				robot.stopRightMotor();
			}
		}
		
		// sets the odometer at 0 degrees, the actual orientation of the robot
		odo.setPosition(new double[] {0, 0, 0}, new boolean [] {true, true, true});
		
		// turn to 90 degrees to fix the x coordinates
		navigation.turnTo(90);
		
		// sets the robot heading towards the y-axis
		robot.setForwardSpeed(FORWARD_SPEED);
		
		// this will make the robot stop on the y axis correcting the x coordinates
		// will move each tire until it reachers a gridline, stopping directly on the line
		while (robot.leftMotorMoving() || robot.rightMotorMoving()) {
			if (leftLight.getSecondOrderDerivative() > gridLineIntensity) {
				Sound.beep();
				robot.stopLeftMotor();
			}
			if (rightLight.getSecondOrderDerivative() > gridLineIntensity) {
				Sound.beep();
				robot.stopRightMotor();
			}
		}
		
		// sets the odometer at the correct heading and x and y coordinates
		odo.setPosition(new double[] {xCoordinate, yCoordinate, orientation}, new boolean [] {true, true, true});

		// end of localization
	}
}