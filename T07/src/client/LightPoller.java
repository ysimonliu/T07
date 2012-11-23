package client;

import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

// Class that controls the light sensors and makes sure that the data doesn't
// become confused when being called from multiple sources
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
	 * constructor. light sensor passed in from DPM_Client class
	 * @param ls
	 */
	public LightPoller(LightSensor ls) {
		
		this.ls = ls;
		ls.setFloodlight(false);
		this.lightPollerTimer = new Timer(DEFAULT_PERIOD_ULTRASONIC, this);
		this.lightPollerTimer.start();
	}
	
	/**
	 * populate raw value and second order derivative of the middle light sensor reading
	 * at a time interval
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
	 * get the second order derivative of the middle light sensor reading
	 * @return
	 */
	public int getSecondOrderDerivative() {
		return secondOrderDerivative;
	}
	
	/**
	 * get the raw value reading of the middle light sensor reading
	 * @return
	 */
	public int getRawValue() {
		return rawLightValue;
	}	
}