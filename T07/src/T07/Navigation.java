package T07;

import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;

public class Navigation {
		
	private boolean isTurning = false;
	private TwoWheeledRobot robot;
	private LightPoller leftLP, rightLP;
	private Odometer odometer;
	private USPoller usPoller;
	private double turningAngle;
	private static final double THETA_EPSILON = 0.8, EPSILON = 1,	TILE_LENGTH = 30.48;
	private static final int ROTATION_SPEED = 15, THETA_P_X = 90, THETA_P_Y = 0, THETA_N_X = 270, THETA_N_Y = 180;
	private static final int OBSTACLE_DISTANCE = 20, GRID_LINE_THRESHOLD = 50, THETA_TOLERANCE = 20;
	private static double currentTheta;
	private static double[] currentPosition;
	private static boolean[] changeValue;
	
	private static double currentX;
	
	public Navigation(TwoWheeledRobot robot, Odometer odometer, USPoller usPoller, LightPoller leftLP, LightPoller rightLP){
		this.odometer = odometer;
		this.robot = odometer.getTwoWheeledRobot();
		this.usPoller = usPoller;
		this.leftLP = leftLP;
		this.rightLP = rightLP;
	}
	
	public void travelTo(double x, double y) {
		RConsole.println("DEBUG: I'm now travelling to [" + x + "," + y + "]");
		travelAlongYTo(y);
		travelAlongXTo(x);
	}
	
	private void travelAlongXTo(double x) {
		RConsole.println("DEBUG: I'm now travelling along X");
		if (x - odometer.getX() > 0) {
			turnTo(THETA_P_X);
		}
		else {
			turnTo(THETA_N_X);
		}
		robot.setForwardSpeed();
		Delay.msDelay(500); // prevents the robot from reading a line after a rotate
		while(Math.abs(x - (currentX = odometer.getX())) > EPSILON) {
			RConsole.println("our current X position is " + currentX);
			if (usPoller.getFilteredData() < OBSTACLE_DISTANCE) {
				avoidObstacle();
			}
			if (leftLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD || rightLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD) {
				correctOdometer();
			}
		}
		robot.stop();
	}
	
	private void travelAlongYTo(double y) {
		RConsole.println("DEBUG: I'm now travelling along Y");
		if (y - odometer.getY() > 0) {
			turnTo(THETA_P_Y);
		}
		else {
			turnTo(THETA_N_Y);
		}
		robot.setForwardSpeed();
		Delay.msDelay(500); // prevents the robot from reading a line after a rotate
		while(Math.abs(y - odometer.getY()) > EPSILON) {
			if (usPoller.getFilteredData() < OBSTACLE_DISTANCE) {
				avoidObstacle();
			}
			if (leftLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD || rightLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD) {
				correctOdometer();
			}
		}
		robot.stop();
	}

	public void turnTo(double angle) {
		// first stop the robot in motion
		robot.stop();
		// indicate that now is turning
		isTurning = true;
		angle = normalizeAngle(angle);
		turningAngle = normalizeAngle(angle - odometer.getTheta());
		RConsole.println("DEBUG: I'm now turning to " + turningAngle);
		// turning the minimal angle and wait until reached the angle
		if (turningAngle < 180) {
			robot.setRotationSpeed(ROTATION_SPEED);
		}
		else {
			robot.setRotationSpeed(-ROTATION_SPEED);
		}
		while(Math.abs(angle - odometer.getTheta()) > THETA_EPSILON);
		// then stop the robot
		robot.stop();
		isTurning = false;
	}
	
	public void turnAngle(double angle){
		turnTo(odometer.getTheta() + angle);
	}
	
	// helper functions
	private void travelDistance(double distance) {
		robot.setForwardSpeed();
		while (robot.getDisplacement() < distance) {
			if (usPoller.getFilteredData() < OBSTACLE_DISTANCE) {
				avoidObstacle();
			}
			if (leftLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD || rightLP.getSecondOrderDerivative() < GRID_LINE_THRESHOLD) {
				correctOdometer();
			}
		}
		robot.stop();
	}
	
	private void avoidObstacle() {
		// first stop robot
		robot.stop();
		// turn right by 90 degree, then advance one tile length
		turnAngle(90);
		travelDistance(TILE_LENGTH);
		// turn to original angle
		turnAngle(-90);
		robot.setForwardSpeed();
	}
	
	private double normalizeAngle(double angle) {
		while (angle < 0.0) {
			angle = 360.0 + (angle % 360.0);
		}
		return angle % 360.0;
	}
	
	private void correctOdometer() {
		
		while (robot.leftMotor.isMoving() || robot.rightMotor.isMoving()) {
			//RConsole.println("left light sensor reading " + leftLP.getSecondOrderDerivative());
			//RConsole.println("left motor is moving? " + robot.leftMotor.isMoving());
			if (leftLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD && robot.leftMotor.isMoving()) {
				Sound.beep();
				robot.stopLeftMotor();
				//robot.slowRightMotorSpeed();
			}
			if (rightLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD && robot.rightMotor.isMoving()) {
				Sound.beep();
				robot.stopRightMotor();
				//robot.slowLeftMotorSpeed();
			}
		}
		
		// figure out the direction of the robot
				if (Math.abs(currentTheta - THETA_P_Y) < THETA_TOLERANCE){
					// if theta between 20 and 340, robot is moving along the y axis in the positive direction 	
						
					// get corrected heading and position	
					currentPosition = new double[]{0, Math.round(odometer.getY() / TILE_LENGTH) * TILE_LENGTH, THETA_P_Y};
					// only change y value and heading
					changeValue = new boolean[] {false, true, true};

				} else if (Math.abs(currentTheta - THETA_P_X) < THETA_TOLERANCE){
					// if theta between 70 and 110, robot is moving along the x axis in the positive direction
						
					// get corrected position and heading
					currentPosition = new double[]{Math.round(odometer.getX() / TILE_LENGTH) * TILE_LENGTH, 0, THETA_P_X};
					// only change the x value and heading
					changeValue = new boolean[] {true, false, true};
						
				} else if (Math.abs(currentTheta - THETA_N_Y) < THETA_TOLERANCE) {	
					// if theta between 160 and 200, robot is moving along the y axis in the negative direction
						
					// get corrected heading and position
					currentPosition = new double[]{0, Math.round(odometer.getY() / TILE_LENGTH) * TILE_LENGTH, THETA_N_Y};
					// only change the y value and heading
					changeValue = new boolean[] {false, true, true};	
						
				} else if (Math.abs(currentTheta - THETA_N_X) < THETA_TOLERANCE) {				
					 // if theta between 250 and 290, robot is moving along the x axis in the negative direction
						
					 // get corrected position and heading
					currentPosition = new double[]{Math.round(odometer.getX() / TILE_LENGTH) * TILE_LENGTH, 0, THETA_N_X};
					 // only change the x value and heading
					changeValue = new boolean[]{true, false, true};		
				}
					
				// sets the odometer to the correct x or y values
				odometer.setPosition(currentPosition, changeValue);	
					
				// starts the robot moving again, will continue the navigation
				robot.setForwardSpeed();
				RConsole.println("Change X to " + currentPosition[0] + " and Y to " + currentPosition[1] + " and heading to " + currentPosition[2]);
	}
}
