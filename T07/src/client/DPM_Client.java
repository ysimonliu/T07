package client;

import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class DPM_Client {
	public enum LSensor {LEFT, MIDDLE, RIGHT};

	static private final LightSensor MIDDLE_LIGHT_SENSOR = new LightSensor(SensorPort.S1);
	
	public static void main(String[] args) {
		LightPoller lightPoller = new LightPoller(MIDDLE_LIGHT_SENSOR);
		//get connection with the master brick
		CommunicationClient communicationClient = new CommunicationClient();
		//start communication with master brick
		CommunicationController communicationController = new CommunicationController(lightPoller,communicationClient);
		
		// once the escape button is pressed, the robot will exit
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
