package client;

import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class is the client-side version of the light poller. 
 * It polls the light sensor reading given a light sensor,
 * and process it through the second derivative filter
 * @author Simon Liu
 *
 */
public class LightPoller implements TimerListener{
	
	// private variable that stores the controlled USValue
	private int rawLightValue = 0;
	private int firstOrderDerivative = 0;
	private int secondOrderDerivative = 0;
	private int previousValue = 0;
	private int previousFirstOrderDerivative = 0;
	private Timer lightPollerTimer;
	private final int DEFAULT_PERIOD_ULTRASONIC = 20; // Period for which the timerlistener will sleep (Adjust if necessary)
	private LightSensor ls;


	/**
	 * Constructs with a light sensor
	 * @param ls
	 */
	public LightPoller(LightSensor ls) {
		
		this.ls = ls;
		ls.setFloodlight(false);
		this.lightPollerTimer = new Timer(DEFAULT_PERIOD_ULTRASONIC, this);
		this.lightPollerTimer.start();
	}
	
	/**
	 * Reads the light sensor periodically and process the values to second derivative
	 */
	public void timedOut() {
		// add the newly read distance to replace the oldest element in the array
		previousFirstOrderDerivative = firstOrderDerivative;
		previousValue = rawLightValue;
		rawLightValue = ls.getNormalizedLightValue();
		firstOrderDerivative = rawLightValue - previousValue;
		secondOrderDerivative = firstOrderDerivative - previousFirstOrderDerivative;
	}
	
	/**
	 * Returns the second order derivative of the light sensor poller
	 * @return - the second order derivative of the light sensor reading
	 */
	public int getSecondOrderDerivative() {
		return secondOrderDerivative;
	}
	
	/**
	 * Returns the most recent raw ultrasonic sensor reading
	 * @return the raw ultrasonic sensor reading
	 */
	public int getRawValue() {
		return rawLightValue;
	}	
}