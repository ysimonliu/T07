package T07;

import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;
import java.util.Arrays;

// Class that controls the US sensors of the robot, it will function to filter data and
// control pinging of the sensors so US information is stable, also extends TimerListener

public class USPoller implements TimerListener{
	
	// private variable that stores the controlled USValue
	private int rawUSValue, filteredUSValue;
	private Timer usPollerTimer;
	private final int DEFAULT_PERIOD_ULTRASONIC = 60; // Period for which the timerlistener will sleep (Adjust if necessary)
	private UltrasonicSensor us;
	private TwoWheeledRobot robot;
	private static int[] readingRecords = new int[10];
	private static int counter;
	
	// Constructor for USPoller
	public USPoller(TwoWheeledRobot robot) {
		this.robot = robot;
		this.us = robot.leftUSSensor;
		this.usPollerTimer = new Timer(DEFAULT_PERIOD_ULTRASONIC, this);
		this.usPollerTimer.start();
		counter = 0;
	}
	
	// TimerListener method that sets filtered US data readings, also controls pinging rates
	public void timedOut() {

		// do a ping
		us.ping();

		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}

		// add the newly read distance to replace the oldest element in the array
		rawUSValue = us.getDistance();
		readingRecords[counter % 10] = rawUSValue;
		// get the median value of the array
		filteredUSValue = getReadingRecordsMedian();
		// increment the counter to move to the element in the array, aka the oldest element to be replaced
		counter++;
		
	}
	
	// get median will return the median value of the last eleven ultrasonic sensor readings
	private int getReadingRecordsMedian() {
		int medianValue;
		// sort the reading records and copy it to another array
		int[] sortedReadings = sortReadingRecords(Arrays.copyOf(readingRecords, readingRecords.length)); 
		// if the number of elements in the array is odd, get the value of the middle index of the array
		if (sortedReadings.length % 2 == 1) {
			medianValue = sortedReadings[sortedReadings.length / 2 + 1];
		}
		// else calculate the mean value of the value of the middle two elements
		else {
			medianValue = (sortedReadings[sortedReadings.length / 2] + sortedReadings[sortedReadings.length / 2 + 1] ) / 2;
		}
		return medianValue;
	}

	// this is a helper function to sort an array
	private int[] sortReadingRecords(int[] arraysToBeSorted) {
		Arrays.sort(arraysToBeSorted);
		return arraysToBeSorted;
	}
		
	// getter method that returns the filteredUSValue
	public int getFilteredData() {
		return filteredUSValue;
	}
	
	// getter for the unprocessed data
	public int getRawData() {
		return rawUSValue;
	}
}