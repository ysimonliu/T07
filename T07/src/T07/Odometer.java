package T07;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class controlls the odometer
 * @author Simon Liu, Ashley Simpson
 */
public class Odometer implements TimerListener {
	// ALL THESE CODE & COMMENTS ARE GIVEN BY THE TA FOR LOCALIZER
	// We do not have any comments for this class
	public static final int DEFAULT_PERIOD = 25;
	private TwoWheeledRobot robot;
	private Timer odometerTimer;
	private Navigation nav;
	private Object lock;
	private double x, y, theta;
	private double [] oldDH, dDH;

	/**
	 * Constructs the odometer
	 * @param robot - current robot
	 * @param period - time interval for updating the odometer
	 * @param start - starts odometer right away
	 */
	public Odometer(TwoWheeledRobot robot, int period, boolean start) {
		// initialize variables
		this.robot = robot;
		odometerTimer = new Timer(period, this);
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		oldDH = new double [2];
		dDH = new double [2];
		lock = new Object();
		if (start) {
			odometerTimer.start();
		}
	}

	/**
	 * Constructs the odometer
	 * @param robot - current odometer
	 */
	public Odometer(TwoWheeledRobot robot) {
		this(robot, DEFAULT_PERIOD, false);
	}

	/**
	 * Constructs the odometer
	 * @param robot - current robot
	 * @param start - starts odometer right away
	 */
	public Odometer(TwoWheeledRobot robot, boolean start) {
		this(robot, DEFAULT_PERIOD, start);
	}

	/**
	 * Constructs the odometer
	 * @param robot - current robot
	 * @param period - time interval for updating the odometer
	 */
	public Odometer(TwoWheeledRobot robot, int period) {
		this(robot, period, false);
	}

	/**
	 * Starts the updating of the odometer
	 */
	public void timedOut() {
		robot.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (lock) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.sin(Math.toRadians(theta));
			y += dDH[0] * Math.cos(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}

	/**
	 * Gets the current position
	 * @param pos - empty array whose elements will be updated with the current position
	 */
	public void getPosition(double [] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}

	/**
	 * Returns the current X position
	 * @return current X
	 */
	public double getX() {
		synchronized (lock) {
			return this.x;
		}
	}

	/**
	 * Returns the current Y position
	 * @return current Y
	 */
	public double getY() {
		synchronized (lock) {
			return this.y;
		}
	}

	/**
	 * Returns the heading angle
	 * @return current heading angle
	 */
	public double getTheta() {
		synchronized (lock) {
			return this.theta;
		}
	}

	/**
	 * Returns the current robot
	 * @return current robot
	 */
	public TwoWheeledRobot getTwoWheeledRobot() {
		return robot;
	}

	/**
	 * Sets the current position
	 * @param pos - current position to set
	 * @param update - an array to decide which value of the odometer to update
	 */
	public void setPosition(double [] pos, boolean [] update) {
		synchronized (lock) {
			if (update[0]) x = pos[0];
			if (update[1]) y = pos[1];
			if (update[2]) theta = pos[2];
		}
	}

	/**
	 * Normalizes the angle to 0 to 360 degrees
	 * @param angle - the angle to be normalized
	 * @return the angle after normalization
	 */
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		return angle % 360.0;
	}

	/**
	 * Calculates the minimal angle from a to b
	 * @param a - starting angle
	 * @param b - angle to turn to
	 * @return the minimal turning angle
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
