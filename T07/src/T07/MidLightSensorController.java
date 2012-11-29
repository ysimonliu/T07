package T07;

import lejos.nxt.NXTRegulatedMotor;

/**
 * This controlles the light sensor to turn and process the readings
 * @author Simon Liu
 *
 */
public class MidLightSensorController {
		
		private TwoWheeledRobot robot;
		private NXTRegulatedMotor lsMotor;
		private static int maxReading = -1, currentReading;
		private static double maxReadingOffsetAngle = 0;
		private final static int SWIPE_RANGE = 90;
		private final static int SWIPE_ANGLE = SWIPE_RANGE / 2;
		private final static int LIGHT_SENSOR_ROTATION_SPEED = 50;
		
		/**
		 * constructor for this class light sensor controller
		 * @param robot - the current robot
		 */
		public MidLightSensorController(TwoWheeledRobot robot){
			this.robot = robot;
			this.lsMotor = robot.lightSensorMotor;
		}
		
		/**
		 * Swipes 90 degree, we will return the max reading during the swipe
		 * before start, need to manually set the mid light sensor to straight ahead
		 * @return the max reading during the swipe
		 */
		public int findMaxReading(){
			// reset the two variables that record the maxReading and maxReadingOffsetAngle
			maxReading = -1;
			maxReadingOffsetAngle = 0;
			// manually set initial position to straight ahead
			lsMotor.setSpeed(LIGHT_SENSOR_ROTATION_SPEED);
			// turn motor to the start position
			lsMotor.rotateTo(-SWIPE_ANGLE, false);
			// now start swiping to 45 degree
			lsMotor.rotateTo(SWIPE_ANGLE, true);
			while (lsMotor.isMoving()){
				if ((currentReading = robot.getMidLightSensorReading()) > maxReading){
					maxReading = currentReading;
					// the angle is the opposite sign with the tacho count because the motor is upside down
					maxReadingOffsetAngle = -robot.lightSensorMotor.getTachoCount();
				}
			}
			// return to straight ahead position
			lsMotor.rotateTo(0);
			return maxReading;
		}
		
		/**
		 * Swipes 90 degree, we will return the max reading offset angle during the swipe
		 * before start, need to manually set the mid light sensor to straight ahead
		 * @return the max reading offset angle during the swipe
		 */
		public double findMaxAngle(){
			// reset the two variables that record the maxReading and maxReadingOffsetAngle
			maxReading = -1;
			maxReadingOffsetAngle = 0;
			// manually set initial position to straight ahead
			lsMotor.setSpeed(LIGHT_SENSOR_ROTATION_SPEED);
			// turn motor to the start position
			lsMotor.rotateTo(-SWIPE_ANGLE, false);
			// now start swiping to 45 degree
			lsMotor.rotateTo(SWIPE_ANGLE, true);
			while (lsMotor.isMoving()){
				if ((currentReading = robot.getMidLightSensorReading()) > maxReading){
					maxReading = currentReading;
					// the angle is the opposite sign with the tacho count because the motor is upside down
					maxReadingOffsetAngle = -robot.lightSensorMotor.getTachoCount();
				}
			}
			// return to straight ahead position
			lsMotor.rotateTo(0);
			return maxReadingOffsetAngle;
		}
}
