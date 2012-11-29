package T07;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class displays all the information to the master brick monitor
 * @author Simon
 *
 */
public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odometer;
	private Timer lcdTimer;
	private USPoller usPoller;
	private CommunicationController communicationController;
	private LightPoller lpl;
	private LightPoller lpr;
	//private TwoWheeledRobot robot;

	// arrays for displaying data
	private double [] position;

	/**
	 * Constructs the LCDInfo
	 * @param odometer - odometer
	 * @param usPoller - ultrasonic sensor poller
	 * @param communicationController - communication controller
	 * @param lpl - left light sensor poller
	 * @param lpr - right light sensor poller
	 */
	public LCDInfo(Odometer odometer, USPoller usPoller, CommunicationController communicationController, LightPoller lpl, LightPoller lpr) {
		this.odometer = odometer;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.usPoller = usPoller;
		this.communicationController = communicationController;
		this.lpl = lpl;
		this.lpr = lpr;
		//this.robot = odometer.getTwoWheeledRobot();

		// initialize the arrays for displaying data
		position = new double [3];

		// start the timer
		lcdTimer.start();
	}

	/**
	 * Refreshes the monitor periodically with current X, current Y, current heading,
	 * left light sensor reading, right light sensor reading
	 */
	public void timedOut() { 
		odometer.getPosition(position);
		// print to the LCD screen
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString(formattedDoubleToString(position[0], 2), 3, 0);
		LCD.drawString(formattedDoubleToString(position[1], 2), 3, 1);
		LCD.drawString(formattedDoubleToString(position[2], 2), 3, 2);
		LCD.drawString("LLS :" + lpl.getSecondOrderDerivative(), 0, 3);
		LCD.drawString("RLS :" + lpr.getSecondOrderDerivative(), 0, 4);
		// print to the RConsole Viewer for debug's convenience
		//RConsole.println("LLS :" + lpl.getSecondOrderDerivative());
		/*RConsole.println("Y:" + formattedDoubleToString(position[1], 2));
		RConsole.println("Theta:" + formattedDoubleToString(position[2], 2));
		RConsole.println("LeftUS Raw:" + usPoller.getRawData());
		RConsole.println("LeftUS Processed:" + usPoller.getFilteredData());
		RConsole.println("LeftLS Raw:" + lpl.getRawData());
		RConsole.println("LeftLS Processed:" + lpl.getFilteredData());
		RConsole.println("RightLS Raw:" + lpr.getRawData());
		RConsole.println("RightLS Processed:" + lpr.getFilteredData());
		*/
	}

	/**
	 * Helper function to formats a pass in double value to string in a certain number of decimal places
	 * @param x - double value to be formatted and converted to string
	 * @param places - number of decimal places
	 * @return String after conversion
	 */
	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;

		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";

		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long)x;
			if (t < 0)
				t = -t;

			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}

			result += stack;
		}

		// put the decimal, if needed
		if (places > 0) {
			result += ".";

			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long)x);
			}
		}

		return result;
	}

}