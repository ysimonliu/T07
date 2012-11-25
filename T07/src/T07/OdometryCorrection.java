package T07;

import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

public class OdometryCorrection extends Thread {

	private boolean odometryCorrectionOn = false;
	private TwoWheeledRobot robot;
	private LightPoller leftLP, rightLP;
	private Odometer odometer;
	private static final int GRID_LINE_THRESHOLD = 100;
	private static final double TILE_LENGTH = 30.48;
	private static final int THETA_P_X = 90, THETA_P_Y = 0, THETA_N_X = 270, THETA_N_Y = 180, THETA_TOLERANCE = 20;
	private static double currentTheta;
	private static double[] currentPosition;
	private static boolean[] changeValue;
	
	public OdometryCorrection (TwoWheeledRobot robot, Odometer odometer, LightPoller leftLP, LightPoller rightLP){
		this.robot = robot;
		this.leftLP = leftLP;
		this.rightLP = rightLP;
		this.odometer = odometer;
		odometryCorrectionOn = false;
		// start upon instantiation
		this.start();
	}
	
	public void run(){
		RConsole.println("Now I'm inside run method of odometry correction");
		while(true){
			if (odometryCorrectionOn){
				correctOdometer();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void correctOdometer() {
		RConsole.println("Now I am correcting my odometer");
		while (robot.leftMotorMoving() || robot.rightMotorMoving()) {
			if (leftLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD) {
				Sound.beep();
				robot.stopLeftMotor();
				robot.slowRightMotorSpeed();
			}
			if (rightLP.getSecondOrderDerivative() > GRID_LINE_THRESHOLD) {
				Sound.beep();
				robot.stopRightMotor();
				robot.slowLeftMotorSpeed();
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
	}
	
	public void turnOn() {
		odometryCorrectionOn = true;
	}
	
	public void turnOff() {
		odometryCorrectionOn = false;
	}
	
}
