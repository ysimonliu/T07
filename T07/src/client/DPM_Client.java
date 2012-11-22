package client;

import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class DPM_Client {
	public enum LSensor {LEFT, MIDDLE, RIGHT};

	static final LightSensor MIDDLE_LIGHT_SENSOR = new LightSensor(SensorPort.S1);
	static final UltrasonicSensor RIGHT_US_SENSOR = new UltrasonicSensor(SensorPort.S2);
	static final NXTRegulatedMotor LEFT_CLAW_MOTOR = Motor.A;
	static final NXTRegulatedMotor RIGHT_CLAW_MOTOR = Motor.B;
	static final NXTRegulatedMotor CLAW_LIFT_MOTOR = Motor.C;
	
	public static void main(String[] args) {
		LightPoller lightPoller = new LightPoller(MIDDLE_LIGHT_SENSOR);
		USPoller usPoller = new USPoller(RIGHT_US_SENSOR);
		//get connection with the master brick
		CommunicationClient communicationClient = new CommunicationClient();
		//start communication with master brick
		CommunicationController communicationController = new CommunicationController(lightPoller, usPoller, communicationClient);
		
		// once the escape button is pressed, the robot will exit
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
