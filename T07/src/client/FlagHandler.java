package client;

import lejos.nxt.NXTRegulatedMotor;
import T07.Navigation;
import T07.Odometer;
import T07.TwoWheeledRobot;
import T07.USPoller;

// class that controls the flag handling performed by the robot
public class FlagHandler {
	private NXTRegulatedMotor leftClawMotor = DPM_Client.LEFT_CLAW_MOTOR;
	private NXTRegulatedMotor rightClawMotor = DPM_Client.RIGHT_CLAW_MOTOR;
	private NXTRegulatedMotor liftRaiseMotor = DPM_Client.CLAW_LIFT_MOTOR;
	private static int openAngle = 60, distanceClawRaised = 0;
	
	// Constructor
	public FlagHandler () {
		distanceClawRaised = 0;
	}
	
	// public method that picks up the flag when it is encountered, called from the searcher class
	public void pickUp() {
		
		//if (communicationController.getMiddleUSSensorValue() <= requiredBlockDistance) {
		
		//} //TODO: figure out how we know that the block is in grasping distance
		
		// Now block should be in grasping distance, do grabbing
		closeClaws();
		raiseClaws(30);
		
	}
	
	// public method that puts down the flag when the destination is encountered, called from the hider class
	public void putDown() {
		
		lowerClaws(30);
		openClaws();
		
	}
	// method that closes the robot claws
	public void closeClaws() {
		
		leftClawMotor.setSpeed(50); // TODO: check the opening and closing speeds
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(openAngle);
		rightClawMotor.rotate(openAngle);
		leftClawMotor.stop(true);
		rightClawMotor.stop(false);
	}
	
	// TODO: check the rotation direction of the claws
	// method that opens the robot claws
	public void openClaws() {
		
		leftClawMotor.setSpeed(50);
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(-openAngle);
		rightClawMotor.rotate(-openAngle);
		leftClawMotor.stop(true);
		rightClawMotor.stop(false);
	}
	
	// method that raises the claw system, passed an int that is converted to a height for distance of raise
	public void raiseClaws(int distance) {
		
		distanceClawRaised = distanceClawRaised + distance; // this takes into account if the claw is raised from some height to another
		
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance*180); // TODO: check the distance raised by the input parameter, make it roughly accurate, check direction, check lowerclaws too
		liftRaiseMotor.stop(false);
		
	}
	
	// method that lowers the claw system, passed an int that is converts to a height for distance to lower
	public void lowerClaws(int distance) {
		
		distanceClawRaised = distanceClawRaised - distance; // this takes into account the amount the claw height will be displaced by
		
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance*180);
		liftRaiseMotor.stop(false);
	}
}