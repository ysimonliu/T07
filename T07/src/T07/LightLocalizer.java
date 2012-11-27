// EVENTUALLY NEED TO COMBINE BOTH LIGHTLOCALIZER AND USLOCALIZER INTO ONE

package T07;

import lejos.nxt.*;


public class LightLocalizer{
	// initialize the odometer, twoWheeledRobot, lightSensor and Navigation
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightPoller leftLight;
	private LightPoller rightLight;
	private Navigation2 navigation;
	public static double ROTATION_SPEED = 3, FORWARD_SPEED = 3;
	// the intensity of the grid line on the tile, to avoid using magic numbers
	private static int gridLineIntensity = 50;
	private static final double tileLength = 30.48;
	private double xCoordinate = 0;
	private double yCoordinate = 0;
	private int orientation = 0;
	
	// constructor
	public LightLocalizer(Odometer odo, LightPoller leftLight, LightPoller rightLight, Navigation2 navi) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.leftLight = leftLight;
		this.rightLight = rightLight;
		this.navigation = navi;
	}
	
	// Method that controls light localization
	public void doLocalization(int corner) {
		
		// depending on the starting corner the localization will be different
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
			xCoordinate = 0;
			yCoordinate = 10*tileLength;
			orientation = 0;
		}
		
		// sets the robot heading towards the x-axis
		robot.setForwardSpeed(FORWARD_SPEED);
		
		// this will make the robot stop on the x axis correcting the y coordinates
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

		// end of localization, TODO: Maybe use this to correct the odometer reading every so often
	}
}