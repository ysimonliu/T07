package T07;

import lejos.nxt.*;
import lejos.util.Delay;

/** class that focuses on searcher for the flag and getting a flag from a known location, this depends on role
 * 
 * @author asimps12
 *
 */
public class Searcher {
	
	private Odometer odometer;
	private Navigation navigation;
	private TwoWheeledRobot robot;
	private LightPoller middleLight;
	private USPoller middlePoller;
	private CommunicationController communicationController;
	private int startX;
	private static final int TILE_UNSEARCHED = 0;
	private static final int TILE_SEARCHED = 1;
	private static final int TILE_OBJECT = 2;
	private int startY;
	private int[][] field  = new int[12][12]; // will store field information for use by the searcher algorithm
	private int lightBeaconThreshold = 50; // minimum light value that exits the searching algorithm and moves robot towards the light source (will be close)
	private int lightBeaconMaxValue; // light value that is detected when in front of the beacon (will be calibrated)
	private int desiredBeaconDistance = 20; // distance that is desired from beacon to grabbing arm
	private static final double tileLength = 30.48;
	
	//Constructor
	public Searcher(Odometer odometer, Navigation navigation, LightPoller middleLight, USPoller middlePoller,
			CommunicationController communicationController, int startX, int startY) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.middleLight = middleLight;
		this.startX = startX;
		this.startY = startY;
		this.middlePoller = middlePoller;
		this.robot = odometer.getTwoWheeledRobot();
		this.communicationController = communicationController;
	}
	
	// method that deals with finding the beacon, will need a sophisticated searching algorithm, may also need a separate method to get
	// get a beacon at a known location, we pass the corner that the robot starts in
	public void findBeacon(int corner) {
		int lightSensor = 900; // will replace with proper value
		int[] position = {0,0};
		
		// this code will move the robot to the desired position on the field and sets the starting position of the field
		if (corner == 1) { // TODO look at the convention for corner tiles
			position[0] = 1;
			position[1] = 1;
			field[position[0]][position[1]-1] = TILE_SEARCHED; // sets the (1,0) tile to searched
			field[position[0]-1][position[1]] = TILE_SEARCHED; // sets the (0,1) tile to searched
			field[position[0]-1][position[1]-1] = TILE_SEARCHED; // sets the (0, 0) tile to searched
			field[position[0]][position[1]] = TILE_SEARCHED; // sets the (1,1) tile to searched
			
			// moves the robot to the center of (1,1)
			navigation.travelTo(odometer.getX() + tileLength/2, odometer.getY() + tileLength/2);
			navigation.turnTo(0);
		} else if (corner == 2) {
			position[0] = 1;
			position[1] = 10;
			field[position[0]][position[1]+1] = TILE_SEARCHED; // sets the (1,11) tile to searched
			field[position[0]-1][position[1]] = TILE_SEARCHED; // sets the (0,10) tile to searched
			field[position[0]-1][position[1]+1] = TILE_SEARCHED; // sets the (11, 11) tile to searched
			field[position[0]][position[1]] = TILE_SEARCHED; // sets the (1,10) tile to searched
			
			// moves the robot to the center of (1,1)
			navigation.travelTo(odometer.getX() + tileLength/2, odometer.getY() - tileLength/2);
			navigation.turnTo(0);
		} else if (corner == 3) {
			position[0] = 10;
			position[1] = 10;
			field[position[0]][position[1]+1] = TILE_SEARCHED; // sets the (10,11) tile to searched
			field[position[0]+1][position[1]] = TILE_SEARCHED; // sets the (11,10) tile to searched
			field[position[0]+1][position[1]+1] = TILE_SEARCHED; // sets the (11, 11) tile to searched
			field[position[0]][position[1]] = TILE_SEARCHED; // sets the (1,1) tile to searched
			
			// moves the robot to the center of (1,1)
			navigation.travelTo(odometer.getX() - tileLength/2, odometer.getY() - tileLength/2);
			navigation.turnTo(0);
		} else if (corner == 4) {
			position[0] = 10;
			position[1] = 1;
			field[position[0]][position[1]-1] = TILE_SEARCHED; // sets the (10,0) tile to searched
			field[position[0]+1][position[1]] = TILE_SEARCHED; // sets the (11,1) tile to searched
			field[position[0]+1][position[1]-1] = TILE_SEARCHED; // sets the (11,0) tile to searched
			field[position[0]][position[1]] = TILE_SEARCHED; // sets the (10,1) tile to searched
			
			// moves the robot to the center of (1,1)
			navigation.travelTo(odometer.getX() - tileLength/2, odometer.getY() + tileLength/2);
			navigation.turnTo(0);
		} 
		
		while (lightBeaconMaxValue > lightSensor) { // will repeat until beacon found
			int lightValue = 0;
			int block = 0; // declares which block to move to, 1 == top, 2 == right, 3 == bottom, 4 == left
			
			//check top of position
			if (position[1]+1 != 12) { // protects against array out of bounds
				if (field[position[0]][position[1]+1] == TILE_UNSEARCHED) { // protects against searched or object
					
					navigation.turnTo(0); // turns to the top block to check light values
					if (lightSensor > lightValue) {
						block = 1;
						lightValue = lightSensor;
						field[position[0]][position[1]+1] = TILE_SEARCHED;
					} else if (middlePoller.getFilteredData() <30) {
						field[position[0]][position[1]+1] = TILE_OBJECT;
					} else {
						field[position[0]][position[1]+1] = TILE_SEARCHED;
					}
				}		
			}
			
			//check right position
			if (position[0]+1 != 12) { // protects against array out of bounds
				if (field[position[0]+1][position[1]] == TILE_UNSEARCHED) { // protects against searched or object
					navigation.turnTo(90); // turns to the right block to check light values
					if (lightSensor > lightValue) {
						block = 2;
						lightValue = lightSensor;
						field[position[0]+1][position[1]] = TILE_SEARCHED;
					} else if (middlePoller.getFilteredData() <30) {
						field[position[0]+1][position[1]] = TILE_OBJECT;
					} else {
						field[position[0]+1][position[1]] = TILE_SEARCHED;
					}
				}		
			}
			
			//check bottom position
			if (position[1]-1 != -1) { // protects against array out of bounds
				if (field[position[0]][position[1]-1] == TILE_UNSEARCHED ) { // protects against searched or object
					navigation.turnTo(180); // turns to the bottom block to check light values
					if (lightSensor > lightValue) {
						block = 3;
						lightValue = lightSensor;
						field[position[0]][position[1]-1] = TILE_SEARCHED;
					} else if (middlePoller.getFilteredData() <30) {
						field[position[0]][position[1]-1] = TILE_OBJECT;
					} else {
						field[position[0]][position[1]-1] = TILE_SEARCHED;
					}
				}		
			}
			
			//check the left position
			if (position[0]-1 != -1) { // protects against array out of bounds
				if (field[position[0]-1][position[1]] == TILE_SEARCHED) { // protects against searched or object
					navigation.turnTo(270); // turns to the bottom block to check light values
					//lightSensor = getLightValue;
					if (lightSensor > lightValue) {
						block = 4;
						lightValue = lightSensor;
						field[position[0]-1][position[1]] = TILE_SEARCHED;
					} else if (middlePoller.getFilteredData() <30) {
						field[position[0]-1][position[1]] = TILE_OBJECT;
					} else {
						field[position[0]-1][position[1]] = TILE_SEARCHED;
					}
				}		
			}
			
			if (block == 1) { // move to top tile
				navigation.travelTo(tileLength*position[0]+(tileLength/2), tileLength*(position[0]+1)+ (tileLength/2));
				position[1] = position[1]+1;
			} else if (block == 2) { // move to the right tile
				navigation.travelTo(tileLength*(position[0]+1)+(tileLength/2), tileLength*position[0]+ (tileLength/2));
				position[0] = position[0]+1;
			} else if (block == 3) { // move to the bottom tile 
				navigation.travelTo(tileLength*position[0]+(tileLength/2), tileLength*(position[0]-1)+ (tileLength/2));
				position[1] = position[1]-1;
			} else if (block == 4) { // move to the left tile
				navigation.travelTo(tileLength*(position[0]-1)+(tileLength/2), tileLength*position[0]+ (tileLength/2));
				position[0] = position[0]-1;
			}
		}		
	}
	
	public void searchLight() {
		
		// all the values that allow the storage of data for light locating
		int i = 0;
		int previousMax = 0;
		int maxLightValue = 0;
		double storedTheta = 0;
		int[] lightValues = new int[25]; // TODO: check that the array doesn't overflow
		double[] thetaValue = new double[25];
		
		// makes the robot do a 360 and detect all the light values around it
		
		robot.rotate(360);
		
		// stores the light values around it and then puts the light values with the corresponding theta
		while (robot.leftMotorMoving() && robot.rightMotorMoving()) {
			lightValues[i] = middleLight.getRawValue();
			thetaValue[i] = odometer.getTheta();
			Delay.msDelay(500);
			i = i + 1;
		}
		
		// previousMax stores the highest light value, storedTheta stores the corresponding theta
		for (int j=0; j<i; j++){
			
			maxLightValue = lightValues[j];
			
			if (maxLightValue >= previousMax) {
				previousMax = maxLightValue;
				storedTheta = thetaValue[j];
			}
		}
		
		// turns to the theta with the highest light reading
		navigation.turnTo(storedTheta);
	}
}