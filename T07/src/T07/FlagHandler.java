package T07;

// class that controls the flag handling performed by the robot
public class FlagHandler {
	private USPoller usPoller; // may need this to get accurate distance of flag, for grabbing
	private TwoWheeledRobot robot;
	private Odometer odometer;
	private Navigation navigation;
	private int requiredBlockDistance = 5; // TODO: calibrate actual distance
	
	// Constructor
	public FlagHandler (Odometer odometer, Navigation navigation, USPoller usPoller) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.robot = odometer.getTwoWheeledRobot();
		this.usPoller = usPoller;
	}
	
	// public method that picks up the flag when it is encountered, called from the searcher class
	public void pickUp() {
		
		/*if (usPoller.getFilteredData() <= requiredBlockDistance) {
		
		}*/ //TODO: figure out how we know that the block is in grasping distance
		
		// Now block should be in grasping distance, do grabbing
		robot.closeClaws();
		robot.raiseClaws(30);
		
	}
	
	// public method that puts down the flag when the destination is encountered, called from the hider class
	public void putDown() {
		
		robot.lowerClaws(30);
		robot.openClaws();
		
	}
}