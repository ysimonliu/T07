// EVENTUALLY NEED TO COMBINE BOTH LIGHTLOCALIZER AND USLOCALIZER INTO ONE

package T07;

import lejos.nxt.*;


public class LightLocalizer{
	// initialize the odometer, twoWheeledRobot, lightSensor and Navigation
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	private Navigation navigation;
	// distance from light sensor to the center of the axle
	private double lightSensorD = 13.5;
	private double[]angles = new double[4];
	public static double ROTATION_SPEED = 30, FORWARD_SPEED = 5;
	// the intensity of the grid line on the tile, to avoid using magic numbers
	private static int gridLineIntensity = 500;
	
	// constructor
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.navigation = new Navigation(odo);
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// we used the following approach to make sure that our swipe will read correct coordinates
		
		// drive right pass the grid line
		travelUntilGridLine();
		// back a lightSensorD
		double odometerReading = odo.getY();
		while (Math.abs(odo.getY() - odometerReading) <= lightSensorD) {
			robot.setForwardSpeed(-FORWARD_SPEED);
		}
		robot.setForwardSpeed(0);
		// turn 90 degree
		navigation.turnTo(90);
		// travel until the light sensor detects the grid line again
		travelUntilGridLine();
		// back a lightSensorD
		odometerReading = odo.getX();
		while (Math.abs(odo.getX() - odometerReading) <= lightSensorD) {
			robot.setForwardSpeed(-FORWARD_SPEED);
		}
		robot.setForwardSpeed(0);
	 	// now we are in position
		
	 	//to store the angles light sensor detects grid lines
		int i = 0;
		// exit condition for while loop is to have recorded all four valid readings
 		while(i < 4){
 			// rotate robot
 			robot.setRotationSpeed(ROTATION_SPEED);
 			if(getLsReading() < gridLineIntensity){
 				// once detects a line, beep, and record data
 				Sound.beep();
 				angles[i] = odo.getTheta();
 				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 				i++;
		 	}
 		}
 		robot.setRotationSpeed(0);
 		
 		// sleep 200 ms before proceed to next steps
 		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
 		// use formulas derived from the tutorial slides to correct odometer
 		double CorrectedX = -lightSensorD * Math.cos(Math.toRadians((angles[3] - angles[1]) / 2));
 		double CorrectedY = -lightSensorD * Math.cos(Math.toRadians((angles[2] - angles[0]) / 2));
 		double CorrectedTheta = (angles[3] - angles[1]) / 2 + 295;
 		// actually correct odometer
 		odo.setPosition(new double[] {CorrectedX, CorrectedY, CorrectedTheta}, new boolean [] {true, true, true});
 		// travel to (0,0) point and turn to 0 degree using the navigation class
 		navigation.travelTo(0,0);
 		navigation.turnTo(0);
	}
	
	// helper function to travel until the grid line
	private void travelUntilGridLine() {
		// robot travel until the light sensor detects grid line
		while (getLsReading() > gridLineIntensity) {
			robot.setForwardSpeed(FORWARD_SPEED);
		}
		robot.setForwardSpeed(0);
	}
	
	// get the light sensor reading
	private int getLsReading() {
		return ls.readNormalizedValue();
	}

}