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
	public static double ROTATION_SPEED = 30, FORWARD_SPEED = 5;
	// the intensity of the grid line on the tile, to avoid using magic numbers
	private static int gridLineIntensity = 500;
	
	// constructor
	public LightLocalizer(Odometer odo, LightPoller leftLight, LightPoller rightLight, Navigation navi) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.leftLight = leftLight;
		this.rightLight = rightLight;
		this.navigation = navi;
	}
	
	// COMMENTED OUT BY ASHLEY OLD LIGHTSENSOR LOCALIZATION METHOD
	
	/*
	public void doLocalization() {
		// drive to location listed in tutorial
		// we used the following approach to make sure that our swipe will read correct coordinates
		
		// drive right pass the grid line
		travelUntilGridLine();
		// back a lightSensorD
		double odometerReading = odo.getY();
		while (Math.abs(odo.getY() - odometerReading) <= lightSensorD) {
			robot.setForwardSpeed(-FORWARD_SPEED);
		}
		robot.setForwardSpeed(0);
		// turn 90 degree
		navigation.turnTo(90);
		// travel until the light sensor detects the grid line again
		travelUntilGridLine();
		// back a lightSensorD
		odometerReading = odo.getX();
		while (Math.abs(odo.getX() - odometerReading) <= lightSensorD) {
			robot.setForwardSpeed(-FORWARD_SPEED);
		}
		robot.setForwardSpeed(0);
	 	// now we are in position
		
	 	//to store the angles light sensor detects grid lines
		int i = 0;
		// exit condition for while loop is to have recorded all four valid readings
 		while(i < 4){
 			// rotate robot
 			robot.setRotationSpeed(ROTATION_SPEED);
 			if(getLsReading() < gridLineIntensity){
 				// once detects a line, beep, and record data
 				Sound.beep();
 				angles[i] = odo.getTheta();
 				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 				i++;
		 	}
 		}
 		robot.setRotationSpeed(0);
 		
 		// sleep 200 ms before proceed to next steps
 		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
 		// use formulas derived from the tutorial slides to correct odometer
 		double CorrectedX = -lightSensorD * Math.cos(Math.toRadians((angles[3] - angles[1]) / 2));
 		double CorrectedY = -lightSensorD * Math.cos(Math.toRadians((angles[2] - angles[0]) / 2));
 		double CorrectedTheta = (angles[3] - angles[1]) / 2 + 295;
 		// actually correct odometer
 		odo.setPosition(new double[] {CorrectedX, CorrectedY, CorrectedTheta}, new boolean [] {true, true, true});
 		// travel to (0,0) point and turn to 0 degree using the navigation class
 		navigation.travelTo(0,0);
 		navigation.turnTo(0);
	}
	*/
	
	// Method that controls light localization
	public void doLocalization() {
		
		// Moves the robot forward towards first gridline
		robot.setForwardSpeed(200); //TODO check the robot speed
		
		// This loop detects the first line (the x axis) and stops directly on it
		while(robot.leftMotorMoving() || robot.rightMotorMoving()) {
			
			if (leftLight.getFilteredData() < 45) { // TODO check the light value reading, will it detect line
				robot.stopLeftMotor();
			}
			if (rightLight.getFilteredData() < 45) { // TODO check the light value reading, will it detect line
				robot.stopRightMotor();
			}	
		}
		double
		odo.setPosition( {0, 0, 90}, {false, false, true} );
		
		navigation.turnTo(0); // Will turn the robot to 0 degrees assuming that pos X is 0 degrees, TODO check convention
		
		// Moves the robot forward towards second gridline
		robot.setForwardSpeed(200); // TODO check the robot speed
		
		// This loop detects the second line (the y axis) and stops directly on it
		while(robot.leftMotorMoving() || robot.rightMotorMoving()) {
			if (leftLight.getFilteredData() < 45) { // TODO check the light value reading
				robot.stopLeftMotor();
			}
			if (rightLight.getFilteredData() < 45) { // TODO check the light value reading
				robot.stopRightMotor();
			}
		}
		
		
				
	}
}