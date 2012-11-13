package T07;
import lejos.nxt.*;

public class TwoWheeledRobot {
	
	// this class contains the measurements of the robot
	public static final double DEFAULT_LEFT_RADIUS = 2.70;
	public static final double DEFAULT_RIGHT_RADIUS = 2.70;
	public static final double DEFAULT_WIDTH = 15.8;
	public NXTRegulatedMotor leftMotor, rightMotor, lightSensorMotor;
	public UltrasonicSensor middleUSSensor, rightUSSensor;
	public LightSensor leftLS, rightLS;
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   UltrasonicSensor middleUSSensor,
						   UltrasonicSensor rightUSSensor,
						   LightSensor leftLS,
						   LightSensor rightLS,
						   double width,
						   double leftRadius,
						   double rightRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftLS = leftLS;
		this.rightLS = rightLS;
		this.middleUSSensor = middleUSSensor;
		this.rightUSSensor = rightUSSensor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, 
						   NXTRegulatedMotor rightMotor, 
						   UltrasonicSensor middleUSSensor, 
						   UltrasonicSensor rightUSSensor,
						   LightSensor leftLS,
						   LightSensor rightLS) {
		this(leftMotor, rightMotor, middleUSSensor, rightUSSensor, leftLS, rightLS,
				DEFAULT_WIDTH, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, 
						   NXTRegulatedMotor rightMotor, 
						   UltrasonicSensor middleUSSensor, 
						   UltrasonicSensor rightUSSensor, 
						   LightSensor leftLS,
						   LightSensor rightLS,
						   double width) {
		this(leftMotor, rightMotor, middleUSSensor, rightUSSensor, leftLS, rightLS,
				width, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	// accessors
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius +
				rightMotor.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius -
				rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, 0);
	}
	
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(0, rotationSpeed);
	}
	
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
	
	// method that returns if leftMotor is moving
	public boolean leftMotorMoving() {
		return leftMotor.isMoving();
	}
	
	// method that returns if rightMotor is moving\
	public boolean rightMotorMoving() {
		return rightMotor.isMoving();
	}
	
	// method that stops the leftmotor only
	public void stopLeftMotor () {
		leftMotor.setSpeed(0);
		leftMotor.stop();
	}
	
	// method that stops the rightmotor only
	public void stopRightMotor () {
		rightMotor.setSpeed(0);
		rightMotor.stop();
	}
	
	/**
	 * both motor rotate a certain degree
	 * @param angle
	 */
	public void rotate(int angle) {
		rightMotor.rotate(angle, true);
		leftMotor.rotate(-angle);
	}
	
	/**
	 * stop two motors at the same time
	 */
	public void stop() {
		rightMotor.stop(true);
		leftMotor.stop();
	}
}