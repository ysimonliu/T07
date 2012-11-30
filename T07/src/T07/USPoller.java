package T07;

import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;
import java.util.Arrays;

/**
 * Class controls the US sensors of the robot, filters and processes data and
 * control pinging of the sensors so US information is stable
 * @author Simon Liu
 *
 */
public class USPoller implements TimerListener{
	
	// private variable that stores the controlled USValue
	private int rawUSValue, filteredUSValue;
	private Timer usPollerTimer;
	private final int DEFAULT_PERIOD_ULTRASONIC = 60; // Period for which the timerlistener will sleep (Adjust if necessary)
	private UltrasonicSensor us;
	private TwoWheeledRobot robot;
	// TODO: test the responsiveness of the sensor readings and change the size of the array
	private static int sizeOfCachedReadings = 7;
	private static int[] readingRecords = new int[sizeOfCachedReadings];
	private static int counter;
	
	/**
	 * Constructs the Ultrasonic Poller
	 * @param robot
	 */
	public USPoller(TwoWheeledRobot robot) {
		this.robot = robot;
		this.us = robot.middleUSSensor;
		this.usPollerTimer = new Timer(DEFAULT_PERIOD_ULTRASONIC, this);
		this.usPollerTimer.start();
		counter = 0;
	}
	
	/**
	 * Polls and processes the ultrasonic sensor readings at a frequency
	 */
	public void timedOut() {

		// do a ping
		us.ping();

		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}

		// add the newly read distance to replace the oldest element in the array
		rawUSValue = us.getDistance();
		readingRecords[counter % sizeOfCachedReadings] = rawUSValue;
		// get the median value of the array
		filteredUSValue = computeReadingRecordsMedian();
		// increment the counter to move to the element in the array, aka the oldest element to be replaced
		counter++;
		
	}
	
	/**
	 * Computes the median value of the last 7 readings
	 * @return the median value of the last 7 readings
	 */
	public int computeReadingRecordsMedian() {
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

	/**
	 * Sorts an array
	 * @param - unsorted array
	 * @return - sorted array
	 */
	private int[] sortReadingRecords(int[] arraysToBeSorted) {
		Arrays.sort(arraysToBeSorted);
		return arraysToBeSorted;
	}
		
	/**
	 * Returns the filtered ultrasonic sensor value
	 * @return the filtered ultrasonic sensor value
	 */
	public int getFilteredData() {
		return filteredUSValue;
	}
	
	/**
	 * Returns the most recent raw reading of the ultrasonic sensor
	 * @return the most recent raw reading of the ultrasonic sensor
	 */
	public int getRawData() {
		return rawUSValue;
	}
}