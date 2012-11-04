package T07;

import lejos.nxt.*;
import lejos.util.TimerListener;

// Class that controls the light sensors and makes sure that the data doesn't
// become confused when being called from multiple sources
public class LightPoller implements TimerListener{
	
	// private variable that store the lightValue
	private int lightValue;
	
	// Constructor of lightpoller
	public LightPoller() {
		// What do we need here?
	}
	
	// timerListener method that controls access to the light sensor
	public void timedOut() {
		
	}
	
	// public method that returns the lightValue for this light sensor
	public int getFilteredData() {
		return lightValue;
	}
}