package T07;

import lejos.nxt.comm.RConsole;

public class Navigation {
		
	private boolean isTurning = false;
	private TwoWheeledRobot robot;
	private Odometer odometer;
	private USPoller usPoller;
	private OdometryCorrection odometryCorrection;
	private double turningAngle;
	private static final double THETA_EPSILON = 0.8, EPSILON = 0.5,	TILE_LENGTH = 30.48;
	private static final int ROTATION_SPEED = 10, THETA_P_X = 90, THETA_P_Y = 0, THETA_N_X = 270, THETA_N_Y = 180, OBSTACLE_DISTANCE = 20;
	
	
	public Navigation(TwoWheeledRobot robot, Odometer odometer, USPoller usPoller, OdometryCorrection odometryCorrection){
		this.odometer = odometer;
		this.odometryCorrection = odometryCorrection;
		this.robot = odometer.getTwoWheeledRobot();
		this.usPoller = usPoller;
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
		odometryCorrection.turnOn();
		robot.setForwardSpeed();
		while(Math.abs(x - odometer.getX()) > EPSILON) {
			if (usPoller.getFilteredData() < OBSTACLE_DISTANCE) {
				avoidObstacle();
			}
		}
		robot.stop();
		odometryCorrection.turnOff();
	}
	
	private void travelAlongYTo(double y) {
		RConsole.println("DEBUG: I'm now travelling along Y");
		if (y - odometer.getX() > 0) {
			turnTo(THETA_P_Y);
		}
		else {
			turnTo(THETA_N_Y);
		}
		odometryCorrection.turnOn();
		robot.setForwardSpeed();
		while(Math.abs(y - odometer.getY()) > EPSILON) {
			if (usPoller.getFilteredData() < OBSTACLE_DISTANCE) {
				avoidObstacle();
			}
		}
		robot.stop();
		odometryCorrection.turnOff();
	}

	public void turnTo(double angle) {
		RConsole.println("DEBUG: I'm now trying to turn to " + angle);
		// indicate that now is turning
		isTurning = true;
		angle = normalizeAngle(angle);
		RConsole.println("DEBUG: Normalized angle is " + angle);
		turningAngle = normalizeAngle(angle - odometer.getTheta());
		RConsole.println("DEBUG: Turning angle is " + angle);
		// turning the minimal angle and wait until reached the angle
		if (turningAngle < 180) {
			robot.setRotationSpeed(ROTATION_SPEED);
			while(odometer.getTheta() - angle > THETA_EPSILON);
		}
		else {
			robot.setRotationSpeed(-ROTATION_SPEED);
			while(angle - odometer.getTheta() > THETA_EPSILON);
		}
		// then stop the robot
		robot.stop();
		isTurning = false;
	}
	
	public void turnAngle(double angle){
		turnTo(odometer.getTheta() + angle);
	}

	public boolean isTurning() {
		return isTurning;
	}
	
	// helper functions
	private void travelDistance(double distance) {
		odometryCorrection.turnOn();
		robot.setForwardSpeed();
		while (robot.getDisplacement() < distance) {
			if (usPoller.getFilteredData() < OBSTACLE_DISTANCE) {
				avoidObstacle();
			}
		}
		robot.stop();
		odometryCorrection.turnOff();
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
}
