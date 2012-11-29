package T07;


import lejos.nxt.LCD;
import lejos.nxt.Sound;
import bluetooth.*;

/**
 * This class receives information by the BT Server written by the TA
 * and translate all information
 * @author Simon Liu
 *
 */
public class BTReceiver {
	
	private int fx;
	private int fy;
	private StartCorner corner;
	private PlayerRole role;
	private int dx;
	private int dy;

	/**
	 * Constructs the BT receiver, connects to the bluetooth server and parse the info received
	 */
	public BTReceiver() {
		BluetoothConnection conn = new BluetoothConnection();
		// as of this point the bluetooth connection is closed again, and you can pair to another NXT (or PC) if you wish
		
		// example usage of Tranmission class
		Transmission t = conn.getTransmission();
		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			corner = t.startingCorner;
			role = t.role;
			//defender will go here to get the flag:
			fx = t.fx;	//flag pos x
			fy = t.fy;	//flag pos y
			
			// attacker will drop the flag off here
			dx = t.dx;	//destination pos x
			dy = t.dy;	//destination pos y
			
			// print out the transmission information to the LCD
			conn.printTransmission();
			Sound.twoBeeps();
		}
		// stall until user decides to end program
	}
	
	/**
	 * return the flagX value
	 * @return the flagX value
	 */
	public int getFx() {
		return this.fx;
	}
	
	/**
	 * return the flagY value
	 * @return the flagY value
	 */
	public int getFy() {
		return this.fy;
	}
	
	/**
	 * return Dx value
	 * @return the Dx value
	 */
	public int getDx() {
		return this.dx;
	}
	
	/**
	 * return the Dy value
	 * @return the Dy value
	 */
	public int getDy() {
		return this.dy;
	}
	
	/**
	 * return the starting corder
	 * @return the starting corner
	 */
	public StartCorner getCorner() {
		return this.corner;
	}
	
	/**
	 * return the role
	 * @return the role
	 */
	public PlayerRole getRole() {
		return this.role;
	}
}
