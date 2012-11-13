// EVENTUALLY NEED TO COMBINE BOTH LIGHTLOCALIZER AND USLOCALIZER INTO ONE

package T07;

import lejos.nxt.*;


public class LightLocalizer{
	// initialize the odometer, twoWheeledRobot, lightSensor and Navigation
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightPoller leftLight;
	private LightPoller rightLight;
	private Navigation navigation;
	// distance from light sensor to the center of the axle
	private double lightSensorD = 18.5;
	private double[]angles = new double[4];
	private double[] leftAngles = new double[4];
	private double[] rightAngles = new double[4];
	public static double ROTATION_SPEED = 20, FORWARD_SPEED = 30;
	// the intensity of the grid line on the tile, to avoid using magic numbers
	private static int gridLineIntensity = 475;
	private static int blackLineDerivativeThreshold = 15;
	
	// constructor
	public LightLocalizer(Odometer odo, LightPoller leftLight, LightPoller rightLight, Navigation navi) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.leftLight = leftLight;
		this.rightLight = rightLight;
		this.navigation = navi;
	}
	
	// Method that controls light localization
	public void doLocalization() {
		navigation.travelForward();
		while (Math.abs(leftLight.getSecondOrderDerivative()) < blackLineDerivativeThreshold) {
		}
		robot.stop();
		double lengthY = 15;
		navigation.travelForwardY(-lengthY);
		
		navigation.turnTo(90);
		
		navigation.travelForward();
		while (Math.abs(leftLight.getSecondOrderDerivative()) < blackLineDerivativeThreshold) {
		}
		
		robot.stop();
		double lengthX = 15;
		navigation.travelForwardX(-lengthX);
		
		//TODO: the following is to be fixed
		
		robot.setRotationSpeed(ROTATION_SPEED);
		
		//to store the angles light sensor detects grid lines
		int i = 0;
		int j = 0;
		// exit condition for while loop is to have recorded all four valid readings
		while(j < 4){
		 	if((Math.abs(leftLight.getSecondOrderDerivative()) < blackLineDerivativeThreshold)) {
		 		// once detects a line, beep, and record data
		 		Sound.beep();
		 		try {
					Thread.sleep(100);
				} catch (Exception e) {
					
				}
		 		leftAngles[i] = odo.getTheta();
		 		i++;
			}
		 	if ((Math.abs(rightLight.getSecondOrderDerivative()) < blackLineDerivativeThreshold)) {
		 		try {
					Thread.sleep(100);
				} catch (Exception e) {
					
				}
		 		Sound.beep();
		 		rightAngles[j] = odo.getTheta();
		 		j++;
		 	}
		}
		robot.stop();
		
		for (i = 0; i < 4; i++) {
			angles[i] = (rightAngles[i] - leftAngles[i]) / 2 + leftAngles[i]; 
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
}