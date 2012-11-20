package T07;

import lejos.nxt.*;
import lejos.nxt.comm.RConsole;


public class Navigation {
	// initialize all variables, both class and instance variables
	private Odometer odometer;
	private TwoWheeledRobot robot;
	private double epsilon = 2.0, thetaEpsilon = 2.0;
	private boolean  isTurning = false;
	private double forwardSpeed = 15, rotationSpeed = 30;
	private USPoller selectedSensor;
	private final int bandCenter = 30, bandwith = 5;
	
		public Navigation(Odometer odometer, USPoller selectedSensor) {
			// constructor
			this.odometer = odometer;
			this.robot = odometer.getTwoWheeledRobot();
			this.selectedSensor = selectedSensor;
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
			// define minimal angle variable
			double minAng;
			// compute the turning angle
		    minAng = 90 - Math.toDegrees(Math.atan2(y - odometer.getY(),x - odometer.getX()));
		   turnTo(minAng);
			// unless I have reached the target X and Y position
		    while (Math.abs(x - odometer.getX() ) > epsilon || Math.abs(y - odometer.getY() ) > epsilon ) {
		    	
		    	// avoid block checker (detects if an object is located in front of the robot and then moves into wall following mode)
		    	/*if (selectedSensor.getFilteredData() < 30) { //TODO test the wall detection value so that robot doesn't hit wall
		    		avoidBlock(); // go into wall following method
		    	}*/	    	
		    	
			    // set robot to move forward
			    robot.setForwardSpeed(forwardSpeed);
		    }
		    // now we have reached the final destination, we can stop and relax now
		    robot.stop();
		    // DEBUG: beep to signal that we have reached the final destination
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
		
		public void avoidBlock() {
			/*
			// TODO change sensors with DPM.USSensor.RIGHT, must change back after wall following
			selectedSensor.changeSensor(DPM.USSensor.RIGHT); // changes the sensor to the right sensor for wall following
			
			turnTo((odometer.getTheta()+270)%360); // turns the robot 90 degrees when an object is detected
			double lockAngle = (odometer.getTheta() + 180) % 360; // this locks the initial angle, used to get out of the wall following mode
			
			// TODO check useless turning!!! This loop will maintain wall following to avoid any objects
			while (odometer.getTheta() > (lockAngle + 20) && odometer.getTheta() < (lockAngle -20)) { // when the robot is facing a specific angle it has avoided the object
				if (selectedSensor.getFilteredData() > (bandCenter + (bandwith))) { // if robot out of bandwith turn torwards wall
					robot.setRotationSpeed(rotationSpeed);
				} else if (selectedSensor.getFilteredData() < (bandCenter - (bandwith))) { // if robot out of bandwith turn from wall
					robot.setRotationSpeed(-rotationSpeed);
				} else { // otherwise continue straight
					robot.setForwardSpeed(forwardSpeed);
				}
			}
			selectedSensor.changeSensor(DPM.USSensor.MIDDLE); // changes the sensor back to the middle sensor after wall following complete
					*/
		}

	}