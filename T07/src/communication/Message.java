package communication;

/**
 * This class defines the message template used between
 * the two bricks during the communication
 * @author Simon Liu
 *
 */
public class Message {
	
	/**
	 * Number of total elements of a local copy of all the values
	 */
	public final static int NUMBER_OF_ELEMENTS = 7;
	/**
	 * The integer representation of the middle light sensor reading in the local array to store all the messages values
	 */
	public final static int MID_LIGHT_SENSOR_VALUE = 0;
	/**
	 * The integer representation of the middle light sensor offset angle in the local array to store all the messages values
	 */
	public final static int MID_LIGHT_SENSOR_THETA = 1;
	/**
	 * The integer representation of the right ultrasonic sensor reading in the local array to store all the messages values
	 */
	public final static int RIGHT_US_SENSOR_VALUE = 2;
	/**
	 * The integer representation of the open claw reading in the local array to store all the messages values
	 */
	public final static int OPEN_CLAW = 3;
	/**
	 * The integer representation of the close claw reading in the local array to store all the messages values
	 */
	public final static int CLOSE_CLAW = 4;
	/**
	 * The integer representation of the claw lift reading in the local array to store all the messages values
	 */
	public final static int RAISE_LIFT = 5;
	/**
	 * The integer representation of the lower claw reading in the local array to store all the messages values
	 */
	public final static int LOWER_LIFT = 6;
	
	private int type;
	private int value;
	private String messageString;
	
	/**
	 * Constructs a message with a type and value
	 * @param type - an integer representation of the type of the message
	 * @param value - the content of the message
	 */
	public Message(int type, int value) {
		this.value = value;
		this.type = type;
		this.messageString = String.valueOf(this.type) + ":" + String.valueOf(this.value);
	}
	
	/**
	 * Constructs a message with a message string
	 * First letter of string is the type, and the rest (integer part) is the value
	 * @param messageString
	 */
	public Message(String messageString) {
		this.messageString = messageString;
		this.value = checkOutValue(this.messageString);
		this.type = checkOutType(this.messageString);
	}
	
	/**
	 * Returns the content of the message in the format of String
	 * @return the content of the message in the format of String
	 */
	public String getString() {
		return this.messageString;
	}
	
	/**
	 * Returns the type of the message
	 * @return the type of the message
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Returns the value of the message
	 * @return the value of the message
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * Parse the message string, and return the first char of the string as the type value
	 * @param Messagetring
	 * @return the type of the message string
	 */
	private int checkOutType(String Messagetring) {
		return Integer.parseInt(messageString.substring(0, 1));
	}
	
	/**
	 * Parse the message string, and return the integer of the rest of the string as the content of the message string
	 * @param messageString
	 * @return the content of the message string
	 */
	private int checkOutValue(String messageString) {
		return Integer.parseInt(messageString.substring(2));
	}

}
