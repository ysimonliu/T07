package T07;

import java.util.Arrays;

import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

// Class that controls the light sensors and makes sure that the data doesn't
// become confused when being called from multiple sources
public class LightPoller implements TimerListener{
	
	// private variable that stores the controlled USValue
	private int rawLightValue, filteredLightValue;
	private Timer lightPollerTimer;
	private final int DEFAULT_PERIOD_ULTRASONIC = 60; // Period for which the timerlistener will sleep (Adjust if necessary)
	private LightSensor ls;
	private TwoWheeledRobot robot;
	// TODO: test the responsiveness of the sensor readings and change the size of the array
	private static int sizeOfCachedReadings = 7;
	private static int[] readingRecords = new int[sizeOfCachedReadings];
	private static int counter;
	
	// Constructor of lightPoller
	public LightPoller(TwoWheeledRobot robot, DPM.LSensor choice) {
		this.robot = robot;
		switch(choice) {
		case LEFT:
			this.ls = robot.leftLS;
		case MIDDLE:
		// FIXME: need to implement the middle sensor once the two brick communication is fixed
		//	this.ls = robot.middleLS;
		case RIGHT:
			this.ls = robot.rightLS;
		}
		this.lightPollerTimer = new Timer(DEFAULT_PERIOD_ULTRASONIC, this);
		this.lightPollerTimer.start();
		counter = 0;
	}
	
	// timerListener method that controls access to the light sensor
	public void timedOut() {
		// add the newly read distance to replace the oldest element in the array
		rawLightValue = ls.getNormalizedLightValue();
		readingRecords[counter % sizeOfCachedReadings] = rawLightValue;
		// get the median value of the array
		filteredLightValue = getReadingRecordsMedian();
		// increment the counter to move to the element in the array, aka the oldest element to be replaced
		counter++;
	}
	
	// get median will return the median value of the last eleven ultrasonic sensor readings
	public int getReadingRecordsMedian() {
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
		return filteredLightValue;
	}
	
	// getter for the unprocessed data
	public int getRawData() {
		return rawLightValue;
	}
}