package T07;

import lejos.nxt.*;
import lejos.util.Delay;


public class Navigation {
	// initialize all variables, both class and instance variables
	private Odometer odometer;
	private TwoWheeledRobot robot;
	private double epsilon = 2.0, thetaEpsilon = 2.0;
	private boolean  isTurning = false;
	private double forwardSpeed = 6, rotationSpeed = 18;
	private USPoller selectedSensor;
	private LightPoller leftLight;
	private LightPoller rightLight;
	private static int gridLineIntensity = 460;
	private static final double tileLength = 30.48;
	private static final int minObjectDistance = 30;
	private long storedSystemTime;
	private double[] currentPosition;
	private boolean[] changeValue;
	
	public Navigation(Odometer odometer, USPoller selectedSensor, LightPoller leftLight, LightPoller rightLight) {
		// constructor
		this.odometer = odometer;
		this.robot = odometer.getTwoWheeledRobot();
		this.selectedSensor = selectedSensor;
		this.leftLight = leftLight;
		this.rightLight = rightLight;
	}
		
	/**
	 * Travel straight forward for a distance in x+ direction.
	 * Assume robot is already facing x+ direction.
	 * @param distance distance to travel forward, travel backward if distance < 0.
	 */
	public void travelForwardX(double distance) {
		double currentX = odometer.getX();
		if (distance > 0) {
			robot.setForwardSpeed(forwardSpeed);
		} else {
			robot.setForwardSpeed(-forwardSpeed);
		}
		
		while (Math.abs(odometer.getX() - currentX - distance) > epsilon) {
			//wait
		}
		robot.stop();	
	}
		
	/**
	 * Travel straight forward for a distance in y+ direction.
	 * Assume robot is already facing y+ direction.
	 * @param distance distance to travel forward, travel backward if distance < 0.
	 */
	public void travelForwardY(double distance) {
		double currentY = odometer.getY();
		if (distance > 0) {
			robot.setForwardSpeed(forwardSpeed);
		} else {
			robot.setForwardSpeed(-forwardSpeed);
		}
			
		while (Math.abs(odometer.getY() - currentY - distance) > epsilon) {
			//wait
		}
		robot.stop();			
	}
		
	public void travelForward() {
		robot.setForwardSpeed(forwardSpeed);	
	}
		
	public void travelTo(double x, double y) {
			
		// Moves the robot in a square, first up along the y axis, and second up along the x axis
			
		// makes sure that the robot turns to the correct heading
		if (y - odometer.getY() < 0) {
			turnTo(180);
		} else {
			turnTo(0);
		}
			
		robot.setForwardSpeed(forwardSpeed);
		Delay.msDelay(500); // prevents the robot from reading a line after a rotate
			
		// moves the robot up the y-axis stops when the desired y-coordinate is reached
		while (Math.abs(y - odometer.getY()) > epsilon) { //FIXME bug here that must be fixed, overshooting range will cause infinitite loop
				
			if (leftLight.getRawValue() < gridLineIntensity) { // checks if the left lightsensor has crossed a gridline, odometry correct if so
				robot.stopLeftMotor();
				storedSystemTime = System.currentTimeMillis();
				while (rightLight.getRawValue() > gridLineIntensity && (System.currentTimeMillis() - storedSystemTime) < 200) {
					// do nothing
				}
				robot.stopRightMotor();
				odometryCorrect();
			}
			if (rightLight.getRawValue() < gridLineIntensity) { // checks if the right lightsensor has crossed a gridline, odometry correct if so
				robot.stopRightMotor();
				storedSystemTime = System.currentTimeMillis();
				while(leftLight.getRawValue() > gridLineIntensity && (System.currentTimeMillis() - storedSystemTime) < 200) {
					// do nothing
				}
				robot.stopLeftMotor();
				odometryCorrect();
			}
			if (selectedSensor.getFilteredData() < minObjectDistance) { // checks for obstacle, stop robot and avoid if so
				robot.stop();
				avoidBlock();
			}
			
		}
			
		robot.stop();
			
		// makes sure that the robot turns to the correct heading
		if (x - odometer.getX() < 0) {
			turnTo(270);
		} else {
			turnTo(90);
		}
			
		robot.setForwardSpeed(forwardSpeed);
		Delay.msDelay(500); // prevents the robot from reading a line after a rotate
			
		// moves the robot up the x-axis stops when the desired x-coordinate is reached
		while (Math.abs(x - odometer.getX()) > epsilon) {
			
			if (leftLight.getRawValue() < gridLineIntensity) {
				robot.stopLeftMotor();
				storedSystemTime = System.currentTimeMillis();
				while (rightLight.getRawValue() > gridLineIntensity && (System.currentTimeMillis() - storedSystemTime) < 200) {
					// do nothing
				}
				robot.stopRightMotor();
				odometryCorrect();
			}
			if (rightLight.getRawValue() < gridLineIntensity) {
				robot.stopRightMotor();
				storedSystemTime = System.currentTimeMillis();
				while(leftLight.getRawValue() > gridLineIntensity && (System.currentTimeMillis() - storedSystemTime) < 200) {
					// do nothing
				}
				robot.stopLeftMotor();
				odometryCorrect();
			}
			if (selectedSensor.getFilteredData() < minObjectDistance) {
				robot.stop();
				avoidBlock();
			}
		}
			
		robot.stop();
		Sound.beep();
			
	}
	
	public void turnTo(double angle) {
		// isTurning is a flag to indicate whether the robot is now turning
	
		isTurning = true;
		while (angle > 360) {
			angle = angle - 360;
		}
		while (angle < 0) {
			angle = angle + 360;
		}
		//RConsole.println("angle:" + angle);
			
		double startingAngle = odometer.getTheta();
		double relativeAngle = angle - startingAngle;
		double speed;
		int fixingAngle;
		if (relativeAngle > 180) relativeAngle = relativeAngle - 360;
		if (relativeAngle < -180) relativeAngle = relativeAngle + 360;
		if (relativeAngle > 0) {
			speed = rotationSpeed;
			fixingAngle = -15;
		} else {
			speed = -rotationSpeed;
			fixingAngle = 15;
		}
		robot.setRotationSpeed(speed);
		while (Math.abs(odometer.getTheta() - angle) > thetaEpsilon) {
			//wait
		}
		// stop the robot
		robot.stop();			
		//robot.rotate(fixingAngle);
		// change the status of the flag
		isTurning = false;
	}
		
	// method that performs odometry correction for the travel to method
	public void odometryCorrect(){
			
		// current theta heading
		double currentTheta = odometer.getTheta();
			
		// check for lines on the field, will stop the robot on the line it is crossing, taken from localization
		while (robot.leftMotorMoving() || robot.rightMotorMoving()) {
			if (leftLight.getRawValue() < gridLineIntensity) {
				Sound.beep();
				robot.stopLeftMotor();
			}
			if (rightLight.getRawValue() < gridLineIntensity) {
				Sound.beep();
				robot.stopRightMotor();
			}
		}
			
		// figure out the direction of the robot
		if (20 >= currentTheta || currentTheta >= 340){
			// if theta between 20 and 340, robot is moving along the y axis in the positive direction 	
				
			// get corrected heading and position	
			currentPosition = new double[]{0, Math.round(odometer.getY() / tileLength) * tileLength, 0};
			// only change y value and heading
			changeValue = new boolean[] {false, true, true};

		} else if (70 <= currentTheta && currentTheta < 110){
			// if theta between 70 and 110, robot is moving along the x axis in the positive direction
				
			// get corrected position and heading
			currentPosition = new double[]{Math.round(odometer.getX() / tileLength) * tileLength, 0, 90};
			// only change the x value and heading
			changeValue = new boolean[] {true, false, true};
				
		} else if (160 <= currentTheta && currentTheta < 200) {	
			// if theta between 160 and 200, robot is moving along the y axis in the negative direction
				
			// get corrected heading and position
			currentPosition = new double[]{0, Math.round(odometer.getY() / tileLength) * tileLength, 180};
			// only change the y value and heading
			changeValue = new boolean[] {false, true, true};	
				
		} else if (250 <= currentTheta && currentTheta < 290) {				
			 // if theta between 250 and 290, robot is moving along the x axis in the negative direction
				
			 // get corrected position and heading
			currentPosition = new double[]{Math.round(odometer.getX() / tileLength) * tileLength, 0, 270};
			 // only change the x value and heading
			changeValue = new boolean[]{true, false, true};		
		}
			
		// sets the odometer to the correct x or y values
		odometer.setPosition(currentPosition, changeValue);	
			
		// starts the robot moving again, will continue the navigation
		robot.setForwardSpeed(forwardSpeed);
			
		Delay.msDelay(500); // allows the robot's light sensors to get off the line and prevent false readings
	}
		
	// method that avoids an approaching object, this is hardcoded
	public void avoidBlock() {
			
		// rotates the robot 90 degrees from its current angle, and stores current angle
		double originalTheta = odometer.getTheta();
		double hardRotate = (odometer.getTheta() + 90) % 360;
		turnTo(hardRotate);
			
		// TODO: May need to deal with an object here... we shall see
			
		// moves the robot forward one tile, will not odometryCorrect at this point
		robot.setForwardSpeed(forwardSpeed);
		robot.moveForwardDistance(tileLength);
			
		// moves the robot to the original heading
		turnTo(originalTheta);
		robot.setForwardSpeed(forwardSpeed);
			
	}

}