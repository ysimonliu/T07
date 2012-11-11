package T07;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odometer;
	private Timer lcdTimer;
	private USPoller usPoller;
	private LightPoller lp;
	//private TwoWheeledRobot robot;

	// arrays for displaying data
	private double [] position;

	public LCDInfo(Odometer odometer, USPoller usPoller, LightPoller lp) {
		this.odometer = odometer;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.usPoller = usPoller;
		this.lp = lp;
		//this.robot = odometer.getTwoWheeledRobot();

		// initialize the arrays for displaying data
		position = new double [3];

		// start the timer
		lcdTimer.start();
	}

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
		// print to the RConsole Viewer for debug's convenience
		RConsole.println("X:" + formattedDoubleToString(position[0], 2));
		RConsole.println("Y:" + formattedDoubleToString(position[1], 2));
		RConsole.println("Theta:" + formattedDoubleToString(position[2], 2));
		RConsole.println("LeftUS Raw:" + usPoller.getRawData());
		RConsole.println("LeftUS Processed:" + usPoller.getFilteredData());
		RConsole.println("LeftLS Raw:" + lp.getRawData());
		RConsole.println("LeftLS Processed:" + lp.getFilteredData());
	}

	// helper function to cast a double to 2 decimal places and return an array
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