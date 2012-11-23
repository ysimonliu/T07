package communication;

/**
 * The message sent between two bricks.
 *
 */
public class Message {
	
	public final static int NUMBER_OF_ELEMENTS = 6;
	public final static int MID_LIGHT_SENSOR_VALUE = 0;
	public final static int MID_LIGHT_SENSOR_THETA = 1;
	public final static int RIGHT_US_SENSOR_VALUE = 2;
	public final static int OPEN_CLAW = 3;
	public final static int CLOSE_CLAW = 4;
	public final static int RAISE_LIFT = 5;
	public final static int LOWER_LIFT = 6;
	
	private int type;
	private int value;
	private String messageString;
	
	public Message(int type, int value) {
		this.value = value;
		this.type = type;
		this.messageString = String.valueOf(this.type) + ":" + String.valueOf(this.value);
	}
	
	public Message(String messageString) {
		this.messageString = messageString;
		this.value = checkOutValue(this.messageString);
		this.type = checkOutType(this.messageString);
	}
	
	public String getString() {
		return this.messageString;
	}
	
	public int getType() {
		return this.type;
	}
	
	public int getValue() {
		return this.value;
	}
	
	private int checkOutType(String Messagetring) {
		return Integer.parseInt(messageString.substring(0, 1));
	}
	
	private int checkOutValue(String messageString) {
		return Integer.parseInt(messageString.substring(2));
	}

}
