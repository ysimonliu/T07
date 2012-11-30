package T07;

import lejos.nxt.*;

/**
 * This class is to perform the hiding task
 * @author Simon Liu
 *
 */
public class Hider {
	private Odometer odometer;
	private Navigation navigation;
	private TwoWheeledRobot robot;
	private USPoller usPoller;
	private MidLightSensorController midLSController;
	private double fieldLength = 30.48 * 11;
	
	
	/**
	 * Constructs the hider with odometer, navigation, usPoller, mid light sensor controller
	 * @param odometer - the robot's odometer
	 * @param navigation - the navigation class of the robot
	 * @param usPoller - the ultrasonic sensor poller
	 * @param midLSController - the middle light sensor controller
	 */
	public Hider(Odometer odometer, Navigation navigation, USPoller usPoller, MidLightSensorController midLSController) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.usPoller = usPoller;
		this.robot = odometer.getTwoWheeledRobot();
		this.midLSController = midLSController;
	}
	
	/**
	 * Picks up the beacon
	 * @param x - flagX
	 * @param y - flagY
	 */
	public void pickUpDefender(int x, int y) {
		navigation.travelTo(x * 30.48, (y - 1) * 30.48, true);
		navigation.travelTo(x * 30.48, y * 30.48, false);
		positionAndGrab();
	}
	
	/**
	 * Positions in front of the robot and grabs the robot
	 */
	public void positionAndGrab() {
		robot.stop();
		navigation.turnTo(45);
		// swipe to offset angles
		navigation.turnTo(midLSController.findMaxAngle() + odometer.getTheta());
		// travel another 12 cm
		robot.resetTachoCountBothWheels();
		robot.setForwardSpeed();
		while(robot.getDisplacement() < 40);
		robot.stop();
		// now at position to pick up
		robot.pickUpFromGround();
		// wait until all actions complete
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		robot.stop();
	}

	/**
	 * Approaches the nearest wall/obstacle and hides the beacon against
	 */
	public void hide() {
		// get robot to travel forward until sees a obstacle in close range
		robot.setForwardSpeed();
		while(usPoller.getFilteredData() > 30);
		robot.stop();
		// travel towards the obstacle for another 8cm
		robot.resetTachoCountBothWheels();
		robot.setForwardSpeed();
		while (robot.getDisplacement() > 8);
		robot.stop();
		putDownAndGo();
	}
	
	/**
	 * Puts down the beacon, opens the claw, and backs up from the beacon
	 */
	public void putDownAndGo() {
		robot.stop();
		// place down the beacon
		robot.placeOntoGround();
		// wait until all actions complete
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Sound.twoBeeps();
		// back off a bit
		robot.resetTachoCountBothWheels();
		robot.setBackwardSpeed();
		while (Math.abs(robot.getDisplacement()) < 25);
		robot.stop();
	}

}