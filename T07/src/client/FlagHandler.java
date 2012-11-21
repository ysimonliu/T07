package client;

import lejos.nxt.NXTRegulatedMotor;
import T07.Navigation;
import T07.Odometer;
import T07.TwoWheeledRobot;
import T07.USPoller;

// class that controls the flag handling performed by the robot
public class FlagHandler {
	private static NXTRegulatedMotor leftClawMotor = DPM_Client.LEFT_CLAW_MOTOR;
	private static NXTRegulatedMotor rightClawMotor = DPM_Client.RIGHT_CLAW_MOTOR;
	private static NXTRegulatedMotor liftRaiseMotor = DPM_Client.CLAW_LIFT_MOTOR;
	private static int openAndClosingAngle = 120, distanceClawRaised = 0;
	
	// Constructor
	public FlagHandler () {
		distanceClawRaised = 0;
	}
	
	// public method that picks up the flag when it is encountered, called from the searcher class
	public void pickUp() {
		closeClaws();
		raiseClaws(30);
	}
	
	// public method that puts down the flag when the destination is encountered, called from the hider class
	public void putDown() {
		lowerClaws(30);
		openClaws();
	}
	
	// method that closes the robot claws
	@SuppressWarnings("deprecation")
	public static void closeClaws() {
		
		leftClawMotor.setSpeed(50); // TODO: check the opening and closing speeds
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(openAndClosingAngle, true);
		rightClawMotor.rotate(openAndClosingAngle, false);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		leftClawMotor.lock(100);
		rightClawMotor.lock(100);
	}
	
	// TODO: check the rotation direction of the claws
	// method that opens the robot claws
	public static void openClaws() {
		
		leftClawMotor.setSpeed(50);
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(-openAndClosingAngle);
		rightClawMotor.rotate(-openAndClosingAngle);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// method that raises the claw system, passed an int that is converted to a height for distance of raise
	@SuppressWarnings("deprecation")
	public static void raiseClaws(int distance) {
		
		distanceClawRaised = distanceClawRaised + distance; // this takes into account if the claw is raised from some height to another
		
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance*180); // TODO: check the distance raised by the input parameter, make it roughly accurate, check direction, check lowerclaws too
		liftRaiseMotor.lock(100);
		
	}
	
	public static void raiseClaws() {
		int distance = 15;
		distanceClawRaised = distanceClawRaised + distance; // this takes into account if the claw is raised from some height to another
		
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance*180); // TODO: check the distance raised by the input parameter, make it roughly accurate, check direction, check lowerclaws too
		liftRaiseMotor.lock(100);
		
	}
	
	// method that lowers the claw system, passed an int that is converts to a height for distance to lower
	public static void lowerClaws(int distance) {
		
		distanceClawRaised = distanceClawRaised - distance; // this takes into account the amount the claw height will be displaced by
		
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance*180);
		liftRaiseMotor.stop(false);
	}
}