package T07;

/** 
 *Searcher class that will start after localization.
 *Travels to the relative center of the field and the commencers search.
 *Uses a memory based algorithm that always travels towards sources of light
 */
public class Searcher {
	
	private Odometer odometer;
	private Navigation navigation;
	private TwoWheeledRobot robot;
	private USPoller middlePoller;
	private Hider hider;
	private MidLightSensorController midLightSensor;
	private static final int TILE_UNSEARCHED = 0;
	private static final int TILE_SEARCHED = 1;
	private static final int TILE_OBJECT = 2;
	private int[][] field  = new int[10][10]; // will store field information for use by the searcher algorithm
	private int lightBeaconThreshold = 420; // light value that is detected when a tile away from the beacon
	private int minimumObjectDistance = 30; // minimum distance to object
	private static final double tileLength = 30.48; // tile length used for calculations
	private final int X = 0;
	private final int Y = 1;
	
	/**
	 * Constructor in the searcher class that sets up the required objects in the find beacon method
	 * 
	 * @param odometer Odometer object that is used to pass a TwoWheeledRobot object
	 * @param navigation Navigation object that is used to move and orientate the robot
	 * @param middlePoller USPoller object that is used to get the distance to objects
	 * @param midLightSensor MidLightSensorController object that retrieves light values for light searching
	 * @param hider Hider object used to mass the public methods for grabbing and dropping the beacon
	 */
	public Searcher(Odometer odometer, Navigation navigation, USPoller middlePoller, MidLightSensorController midLightSensor, Hider hider) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.middlePoller = middlePoller;
		this.robot = odometer.getTwoWheeledRobot();
		this.midLightSensor = midLightSensor;
		this.hider = hider;
	}
	
	/** 
	 * Method that deals with finding the beacon, has a memory based algorithm that will search
	 * the field tile by tile and will always move towards the brightest sources of light.
	 * Initially moves to the center of the field to save on searching time, also the tiles 
	 * directly against the wall will not be entered because robot too large.
	 */
	public void findBeacon() {
		// initializes a lightSensor record and position on the field
		int lightSensor = 0;
		int[] position = {0,0};

		// navigates to the center field and commences search from this point
		navigation.travelTo((4*tileLength) + (tileLength/2), (4*tileLength) + (tileLength/2), true); // travels to the center of the field and commences search from there
		position[0] = 4; // sets the position tracking to (4,4)
		position[1] = 4;
		field[4][4] = TILE_SEARCHED; // sets occupied tile to searched

		// this while loop will keep searching until a light threshold has been reached, determined in testing
		while (lightBeaconThreshold > robot.getMidLightSensorReading()) { // will repeat until beacon found
			int maxLightValue = 0; // sets the max light recorded to nothing, refresh values for searching
			lightSensor = 0; // resets the lightSensor to 0
			int block = 0; // declares which block to move to, 1 == top, 2 == right, 3 == bottom, 4 == left, resets for each search
			
			//check top of position
			if (position[Y]+1 != 10) { // protects against array out of bounds
				if (field[position[X]][position[Y]+1] == TILE_UNSEARCHED) { // protects against searched or object
					navigation.turnTo(0); // turns to the top block to check light values
					lightSensor = midLightSensor.findMaxReading(); // gets the max light value
					if (robot.getMidLightSensorReading() > lightBeaconThreshold) {
						break;
					} // if lightvalue is at the required threshold break from while loop
					if (middlePoller.getFilteredData() < minimumObjectDistance) {
						field[position[X]][position[Y]+1] = TILE_OBJECT; // if object in tile, set as searched and move on
					} else if (lightSensor > maxLightValue) { // if light values are highest here (so far) set as searched and move on
						block = 1;
						maxLightValue = lightSensor;
						field[position[X]][position[Y]+1] = TILE_SEARCHED;
					} else {
						field[position[X]][position[Y]+1] = TILE_SEARCHED; // else just set tile as searched and move on
					}
				}		
			}
			
			//check right position
			if (position[X]+1 != 10) { // protects against array out of bounds
				if (field[position[X]+1][position[Y]] == TILE_UNSEARCHED) { // protects against searched or object
					navigation.turnTo(90); // turns to the right block to check light values
					lightSensor = midLightSensor.findMaxReading();
					if (robot.getMidLightSensorReading() > lightBeaconThreshold) {
						break;
					} // if lightvalue is at the required threshold break from while loop
					if (middlePoller.getFilteredData() < minimumObjectDistance) {
						field[position[X]+1][position[Y]] = TILE_OBJECT; // if object in tile, set as searched and move on
					} else if (lightSensor > maxLightValue) { // if light values are highest here (so far) set as searched and move on
						block = 2;
						maxLightValue = lightSensor;
						field[position[X]+1][position[Y]] = TILE_SEARCHED;
					} else {
						field[position[X]+1][position[Y]] = TILE_SEARCHED; // else just set tile as searched and move on
					}
				}		
			}
			
			//check bottom position
			if (position[Y]-1 != -1) { // protects against array out of bounds
				if (field[position[X]][position[Y]-1] == TILE_UNSEARCHED ) { // protects against searched or object
					navigation.turnTo(180); // turns to the bottom block to check light values
					lightSensor = midLightSensor.findMaxReading();
					if (robot.getMidLightSensorReading() > lightBeaconThreshold) {
						break;
					} // if lightvalue is at the required threshold break from while loop
					if (middlePoller.getFilteredData() < minimumObjectDistance) {
						field[position[X]][position[Y]-1] = TILE_OBJECT; // if object in tile, set as searched and move on
					} else if (lightSensor > maxLightValue) { // if light values are highest here (so far) set as searched and move on
						block = 3;
						maxLightValue = lightSensor;
						field[position[X]][position[Y]-1] = TILE_SEARCHED;
					} else {
						field[position[X]][position[Y]-1] = TILE_SEARCHED; // else just set tile as searched and move on
					}
				}	
			}
			
			//check the left position
			if (position[X]-1 != -1) { // protects against array out of bounds
				if (field[position[X]-1][position[Y]] == TILE_UNSEARCHED) { // protects against searched or object
					navigation.turnTo(270); // turns to the bottom block to check light values
					lightSensor = midLightSensor.findMaxReading();
					if (robot.getMidLightSensorReading() > lightBeaconThreshold) {
						break;
					} // if lightvalue is at the required threshold break from while loop
					if (middlePoller.getFilteredData() < minimumObjectDistance) {
						field[position[X]-1][position[Y]] = TILE_OBJECT; // if object in tile, set as searched and move on
					} else if (lightSensor > maxLightValue) { // if light values are highest here (so far) set as searched and move on
						block = 4;
						maxLightValue = lightSensor;
						field[position[X]-1][position[Y]] = TILE_SEARCHED;
					} else {
						field[position[X]-1][position[Y]] = TILE_SEARCHED; // else just set tile as searched and move on
					}
				}	
			}
			
			// will move to the tile that has the higher relative light readings
			if (block == 1) { // move to top tile and updates the position
				navigation.travelTo(tileLength*(position[0])+(tileLength/2), tileLength*(position[1]+1)+ (tileLength/2),false);
				position[Y] = position[Y]+1;
			} else if (block == 2) { // move to the right tile and updates the position
				navigation.travelTo(tileLength*(position[0]+1)+(tileLength/2), tileLength*(position[1])+ (tileLength/2), false);
				position[X] = position[X]+1;
			} else if (block == 3) { // move to the bottom tile and updates the position
				navigation.travelTo(tileLength*(position[0])+(tileLength/2), tileLength*(position[1]-1)+ (tileLength/2), false);
				position[Y] = position[Y]-1;
			} else if (block == 4) { // move to the left tile and updates the position
				navigation.travelTo(tileLength*(position[0]-1)+(tileLength/2), tileLength*(position[1])+ (tileLength/2), false);
				position[X] = position[X]-1;
			}
		}		
	}
	
	/**
	 *  Method that positions and grabs the beacon.
	 */
	public void positionAndGrabBeacon() {
		hider.positionAndGrab(); // calls a hider method, has an efficient grabbing method		
	}
	
	/**
	 * Method that drops the beacon immediately.
	 */
	public void dropBeacon() {
		hider.putDownAndGo(); // calls a hider method, has an efficient dropping method
	}
}
