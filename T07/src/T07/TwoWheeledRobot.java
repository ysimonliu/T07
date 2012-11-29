package T07;
import lejos.nxt.*;

/**
 * This class defines all measurements and controls all the motors of the robot
 * By doing this, we can avoid motors controlled by multiple classes at the same time and have all measurements defined at one place
 * @author Simon Liu, Ashley Simpson
 *
 */
public class TwoWheeledRobot {
	
	// this class contains the measurements of the robot
	public static final double DEFAULT_LEFT_RADIUS = 2.68;
	public static final double DEFAULT_RIGHT_RADIUS = 2.68;
	public static final double DEFAULT_WIDTH = 16.2;
	private static final int DEFAULT_FORWARD_SPEED = 6, DEFAULT_ROTATION_SPEED = 12;
	public NXTRegulatedMotor leftMotor, rightMotor, lightSensorMotor;
	public UltrasonicSensor middleUSSensor, rightUSSensor;
	public LightSensor leftLS, rightLS, middleLS;
	private  CommunicationController communicationController;
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	
	/**
	 * Constructs the current robot
	 * @param leftMotor - motor that controls the left wheel
	 * @param rightMotor - motor that controls the right wheel
	 * @param lightSensorMotor - motor that controls the light sensor
	 * @param middleUSSensor - the middle ultrasonic sensor
	 * @param leftLS - the left light sensor
	 * @param rightLS - the right light sensor
	 * @param communicationController - communication controller
	 */
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   NXTRegulatedMotor lightSensorMotor,
						   UltrasonicSensor middleUSSensor,
						   LightSensor leftLS,
						   LightSensor rightLS,
						   CommunicationController communicationController) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.lightSensorMotor = lightSensorMotor;
		this.leftLS = leftLS;
		this.rightLS = rightLS;
		this.middleUSSensor = middleUSSensor;
		this.leftRadius = DEFAULT_LEFT_RADIUS;
		this.rightRadius = DEFAULT_RIGHT_RADIUS;
		this.width = DEFAULT_WIDTH;
		this.communicationController = communicationController;
	}
	
	/**
	 * Asks the slave brick to pick up the beacon from ground
	 */
	public void pickUpFromGround() {
		closeClaw();
		liftClaw();
	}
	
	/**
	 * Asks the slave brick to places the beacon on the ground
	 */
	public void placeOntoGround() {
		lowerClaw();
		openClaw();
	}
	
	/**
	 * Asks the slave brick to close the claw
	 */
	public void closeClaw() {
		communicationController.sendCloseClaw();
	}
	
	/**
	 * Asks the slave brick to open the claw
	 */
	public void openClaw() {
		communicationController.sendOpenClaw();
	}
	
	/**
	 * Asks the slave brick to lift the claw
	 */
	public void liftClaw() {
		communicationController.sendRaiseLift();
	}
	
	/**
	 * Asks the slave brick to lower the claw
	 */
	public void lowerClaw() {
		communicationController.sendLowerLift();
	}
	
	/**
	 * Returns the latest mid light sensor reading
	 */
	public int getMidLightSensorReading(){
		return communicationController.getMidLightSensorValue();
	}
	
	/**
	 * Resets the tacho count of the motors that control the wheels
	 */
	public void resetTachoCountBothWheels() {
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
	}
	
	/**
	 * Returns the current communication controller
	 * @return the current communication controller
	 */
	public CommunicationController getCommunicationController(){
		return this.communicationController;
	}
	
	/**
	 * Returns the displacement of the robot
	 * @return the displacement of the robot
	 */
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius +
				rightMotor.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	/**
	 * Returns the heading change of the robot
	 * @return the heading change of the robot
	 */
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius -
				rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	/**
	 * Returns both the heading change and the displacement of the robot
	 * @param data - empty array whose elements will be updated with the heading change and displacement of the robot
	 */
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	/**
	 * Sets the forward speed of both motors
	 * @param speed - forward speed
	 */
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, 0);
	}
	
	/**
	 * Sets the forward speed of both motors with default forward speed
	 */
	public void setForwardSpeed(){
		setForwardSpeed(DEFAULT_FORWARD_SPEED);
	}
	
	/**
	 * Sets the backwards speed of both motors with default backward speed
	 */
	public void setBackwardSpeed(){
		setForwardSpeed(-DEFAULT_FORWARD_SPEED);
	}
	/**
	 * Sets the rotation speed of both motors
	 * @param speed - rotation speed
	 */
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(0, rotationSpeed);
	}
	
	/**
	 * Sets the rotation speed of both motors with default rotation speed
	 */
	public void setRotationSpeed() {
		setRotationSpeed(DEFAULT_ROTATION_SPEED);
	}
	
	/**
	 * Sets the speeds of the motors that control the wheels
	 * @param forwardSpeed - forward speed
	 * @param rotationalSpeed - rotation speed
	 */
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftMotor.setSpeed(900);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightMotor.setSpeed(900);
		else
			rightMotor.setSpeed((int)rightSpeed);
		
	}
	
	/**
	 * Returns whether the left motor is moving
	 * @return true if left motor is moving, false otherwise
	 */
	public boolean leftMotorMoving() {
		return leftMotor.isMoving();
	}
	
	/**
	 * Returns whether the right motor is moving
	 * @return true if right motor is moving, false otherwise
	 */
	public boolean rightMotorMoving() {
		return rightMotor.isMoving();
	}
	
	/**
	 * Returns whether the motors that control the wheels are moving
	 * @return true if left motor or right motor or both are moving, false otherwise
	 */
	public boolean motorsMoving() {
		if (rightMotorMoving() || leftMotorMoving()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Stops the left motor from moving
	 */
	public void stopLeftMotor () {
		leftMotor.stop(true);
	}
	
	/**
	 * Sets the left motor speed
	 * @param speed - speed of left motor
	 */
	public void setLeftMotorSpeed(int speed) {
		leftMotor.setSpeed(speed);
	}
	
	/**
	 * Sets the right motor speed
	 * @param speed - speed of right motor
	 */
	public void stopRightMotor () {
		rightMotor.stop(true);
	}
	
	/**
	 * Sets the right motor speed
	 * @param speed - speed of right motor
	 */
	public void setRightMotorSpeed(int speed) {
		rightMotor.setSpeed(speed);
	}
	
	/**
	 * stop both motors that control the wheels from moving at the same time
	 */
	public void stop() {
		rightMotor.stop(true);
		leftMotor.stop();
	}
}
