package T07;

import lejos.nxt.*;
import lejos.util.TimerListener;

// Class that controls the US sensors of the robot, it will function to filter data and
// control pinging of the sensors so US information is stable, also extends TimerListener
public class USPoller implements TimerListener{
	
	// private variable that stores the controlled USValue
	private int filteredUSValue;
	
	// Constructor for USPoller
	public USPoller() {
		// What do we need here?
	}
	
	// TimerListener method that sets filtered US data readings, also controls pinging rates
	public void timedOut() {
		
	}
	
	// getter method that returns the filteredUSValue
	public int getFilteredData() {
		return filteredUSValue;
	}
}