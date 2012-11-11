package T07;

import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

// Class that controls the US sensors of the robot, it will function to filter data and
// control pinging of the sensors so US information is stable, also extends TimerListener
public class USPoller implements TimerListener{
	
	// private variable that stores the controlled USValue
	private int filteredUSValue;
	private Timer usPollerTimer;
	private final int DEFAULT_PERIOD_ULTRASONIC = 60; // Period for which the timerlistener will sleep (Adjust if necessary)
	private int filterCounter, lastNormalReading; // Limits extraneous values
	private UltrasonicSensor us;
	
	// Constructor for USPoller
	public USPoller(UltrasonicSensor uSensor) {
		this.us = uSensor;
		this.usPollerTimer = new Timer(DEFAULT_PERIOD_ULTRASONIC, this);
		this.usPollerTimer.start();
		this.filterCounter = 0;
	}
	
	// TimerListener method that sets filtered US data readings, also controls pinging rates
	public void timedOut() {
		
		int distance;

		// do a ping
		us.ping();

		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}

		// there will be a delay here
		distance = us.getDistance();

		// filter out the erroneous 255 reading by using a filter. Same ideology with lab 1 except that we keep the last normal(non-255) reading here 
		if (distance == 255) {
			if (filterCounter >= 10){
				distance = 255;
			}
			else {
				distance = lastNormalReading;
			}
			filterCounter++;
		} else {
			lastNormalReading = distance;
			filterCounter = 0;
		}

		filteredUSValue = distance;
	}
	
	
	// getter method that returns the filteredUSValue
	public int getFilteredData() {
		return filteredUSValue;
	}
}