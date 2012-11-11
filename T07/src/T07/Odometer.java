package T07;
import lejos.util.Timer;
import lejos.util.TimerListener;

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

		// start the odometer immediately, if necessary
		if (start)
			odometerTimer.start();
	}

	public Odometer(TwoWheeledRobot robot) {
		this(robot, DEFAULT_PERIOD, false);
	}

	public Odometer(TwoWheeledRobot robot, boolean start) {
		this(robot, DEFAULT_PERIOD, start);
	}

	public Odometer(TwoWheeledRobot robot, int period) {
		this(robot, period, false);
	}

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

	// accessors
	public void getPosition(double [] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}

	public double getX() {
		synchronized (lock) {
			return this.x;
		}
	}

	public double getY() {
		synchronized (lock) {
			return this.y;
		}
	}

	public double getTheta() {
		synchronized (lock) {
			return this.theta;
		}
	}

	public TwoWheeledRobot getTwoWheeledRobot() {
		return robot;
	}

	// mutators
	public void setPosition(double [] pos, boolean [] update) {
		synchronized (lock) {
			if (update[0]) x = pos[0];
			if (update[1]) y = pos[1];
			if (update[2]) theta = pos[2];
		}
	}

	// static 'helper' methods
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		return angle % 360.0;
	}

	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
