package client;

import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;

/**
 * This class will handle all motions regarding the flag handling,
 * including controller of both the lift motor and the claw motors
 * @author Simon Liu
 *
 */
public class FlagHandler {
	private static NXTRegulatedMotor leftClawMotor = DPM_Client.LEFT_CLAW_MOTOR;
	private static NXTRegulatedMotor rightClawMotor = DPM_Client.RIGHT_CLAW_MOTOR;
	private static NXTRegulatedMotor liftRaiseMotor = DPM_Client.CLAW_LIFT_MOTOR;
	private static int distanceClawRaised = 0;
	private static final int MAX_CLAW_HEIGHT = 1000, CLAMP_ANGLE = 45;
	
	/**
	 * First close the claw to grab on the beacon, and then raise the claw
	 */
	public static void pickUp() {
		closeClaws();
		raiseClaws();
	}
	
	/**
	 * First lower the claw, and then open the claw
	 */
	public static void putDown() {
		lowerClaws();
		openClaws();
	}
	
	/**
	 * Closes the claw to grab on the beacon
	 */
	@SuppressWarnings("deprecation")
	public static void closeClaws() {
		
		LCD.drawString("Closing...", 0, 3);
		leftClawMotor.setSpeed(50);
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(CLAMP_ANGLE, true);
		rightClawMotor.rotate(CLAMP_ANGLE, true);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		leftClawMotor.lock(100);
		rightClawMotor.lock(100);
	}
	
	/**
	 * open the claw to release the beacon
	 */
	public static void openClaws() {
		
		LCD.drawString("Opening...", 0, 1);
		leftClawMotor.setSpeed(50);
		rightClawMotor.setSpeed(50);
		leftClawMotor.rotate(-CLAMP_ANGLE, true);
		rightClawMotor.rotate(-CLAMP_ANGLE, true);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * lift the claw to default height
	 */
	public static void raiseClaws() {
		raiseClaws(MAX_CLAW_HEIGHT);
	}
	
	/**
	 * Lifts the claw to the distance
	 * @param distance - the height to lift the claw to
	 */
	@SuppressWarnings("deprecation")
	public static void raiseClaws(int distance) {
		
		LCD.drawString("Raising...", 0, 2);
		distanceClawRaised += distance; // this takes into account if the claw is raised from some height to another
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(distance);
		liftRaiseMotor.lock(100);
		
	}
	
	/**
	 * Lowers the claw to ground height
	 */
	public static void lowerClaws() {
		LCD.drawString("Lowering...", 0, 4);
		liftRaiseMotor.setSpeed(50);
		liftRaiseMotor.rotate(-distanceClawRaised);
	}
}