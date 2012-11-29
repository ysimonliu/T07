package client;


import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class is the client-side version of the US Poller,
 * it will poll the Ultrasonic sensor value, and process it through a mean filter
 * getting both the raw value and the processed mean value as the result
 * @author Simon Liu
 *
 */
public class USPoller implements TimerListener{
	
	// private variable that stores the controlled USValue
	private int rawLightValue = 0;
	private int firstOrderDerivative = 0;
	private int secondOrderDerivative = 0;
	private int previousValue = 0;
	private int previousFirstOrderDerivative = 0;
	private Timer lightPollerTimer;
	private final int DEFAULT_PERIOD_ULTRASONIC = 20; // Period for which the timerlistener will sleep (Adjust if necessary)
	private UltrasonicSensor usSensor;


	/**
	 * constructor of right side US sensor
	 * @param usSensor
	 */
	public USPoller(UltrasonicSensor usSensor) {
	
		this.usSensor = usSensor;
		this.lightPollerTimer = new Timer(DEFAULT_PERIOD_ULTRASONIC, this);
		this.lightPollerTimer.start();
	}
	
	/**
	 * populate raw value and second order derivative of the right side ultrasonic sensor reading
	 * at a time interval
	 */
	public void timedOut() {
		// add the newly read distance to replace the oldest element in the array
		previousFirstOrderDerivative = firstOrderDerivative;
		previousValue = rawLightValue;
		rawLightValue = usSensor.getDistance();
		firstOrderDerivative = rawLightValue - previousValue;
		secondOrderDerivative = firstOrderDerivative - previousFirstOrderDerivative;
		
	}
	
	/**
	 * get the second order derivative of the right side ultrasonic sensor reading
	 * @return
	 */
	public int getSecondOrderDerivative() {
		return secondOrderDerivative;
	}
	
	/**
	 * get the raw value reading of the right side ultrasonic sensor reading
	 * @return
	 */
	public int getRawValue() {
		return rawLightValue;
	}	
}