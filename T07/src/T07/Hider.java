package T07;

import lejos.nxt.*;

// class that focuses on hiding the flag, will also need to deal with depositing the flag
public class Hider {
	private Odometer odometer;
	private Navigation2 navigation;
	private TwoWheeledRobot robot;
	private double fieldLength = 304.8;
	private double smallDistance = 15;
	
	
	//Constructor
	public Hider(Odometer odometer, Navigation2 navigation, TwoWheeledRobot robot) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.robot = robot;
	}
	
	// method that focuses on hiding the flag, may need a second method for just placing the flag
	public void hide() {
		double X = odometer.getX();
		double Y = odometer.getY();
		//find a close wall and place the flag against it
		if(X>=Y){
			navigation.travelTo(fieldLength/2,fieldLength/2);
			//drop the flag
			robot.openClaw();
			navigation.travelForwardX(smallDistance);
			navigation.travelForwardX(-smallDistance);
			
		}
		else{
			navigation.travelTo(0,fieldLength/2);
			robot.openClaw();
			navigation.travelForwardX(-smallDistance);
			navigation.travelForwardX(smallDistance);
		}
		
		this.offField();
		Sound.twoBeeps();
	}
	
	//getting off the field after dropping the flag, this method will find the closest corner
	public void offField(){
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		if(currentX>=currentY){
			if(currentX<=fieldLength/2){
				navigation.travelTo(0, 0);
			}
			if(currentY>=fieldLength/2){
				navigation.travelTo(fieldLength,fieldLength);
			}
			else{
				navigation.travelTo(fieldLength,0);
			}
		}
		else{
			if(currentY<=fieldLength/2){
				navigation.travelTo(0,0);
			}
			if(currentX>=fieldLength/2){
				navigation.travelTo(fieldLength,fieldLength);
			}
			else{
				navigation.travelTo(fieldLength,0);
			}
		}
		robot.stop();
	}

}