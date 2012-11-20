package T07;

/**
 * Sending and receiving data to and from client brick
 */
import communication.Message;

import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class CommunicationController implements TimerListener, Runnable{
	
	private Timer communicationTimer;
	private final int DEFAULT_COMMUNICATION_PERIOD = 60;
	private CommunicationServer communicationServer;
	private LightPoller lightPoller;
	private int[] lightData = new int [3];
	
	public CommunicationController(LightPoller ls, CommunicationServer communicationServer) {
		this.communicationServer = communicationServer;
		this.lightPoller = ls;
		
		// run the data receiver
		new Thread(this).start();
		
		// run the data sender
		this.communicationTimer = new Timer(DEFAULT_COMMUNICATION_PERIOD, this);
		this.communicationTimer.start();
	}
	
	@Override
	/**
	 * sending data at each time out
	 */
	public void timedOut() {
		//TODO:send more data needed.
	}

	@Override
	/**
	 * Creating a thread to receive data
	 */
	public void run() {
		while (true) {
			Message message = this.communicationServer.receive();
			if (message != null) {
				this.lightData[message.getType()] = message.getValue();
			}
		}
		
	}
	
	public void sendLightSensorValue() {
		Message message = new Message(Message.MID_LIGHT_SENSOR_VALUE, this.lightPoller.getRawValue());
		this.communicationServer.sent(message);
	}
	
	public void sendLightSensorHeight() {
		Message message = new Message(Message.MID_LIGHT_SENSOR_HEIGHT, this.lightPoller.getRawValue());
		this.communicationServer.sent(message);
	}
	
	public void sendLightSensorTheta() {
		Message message = new Message(Message.MID_LIGHT_SENSOR_THETA, this.lightPoller.getRawValue());
		this.communicationServer.sent(message);
	}
	
	public int getLightSensorValue() {
		return this.lightData[Message.MID_LIGHT_SENSOR_VALUE];
	}
	
	public int getLightSensorTheta() {
		return this.lightData[Message.MID_LIGHT_SENSOR_THETA];
	}
	
	public int getLightSensorHeight() {
		return this.lightData[Message.MID_LIGHT_SENSOR_HEIGHT];
	}
	
	public int getRightUSSensorValue(){
		return this.lightData[Message.RIGHT_US_SENSOR_VALUE];
	}

}
