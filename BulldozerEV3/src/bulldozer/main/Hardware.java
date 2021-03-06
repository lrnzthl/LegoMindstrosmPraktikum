package bulldozer.main;



import lejos.hardware.Sound;
import lejos.hardware.port.MotorPort;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;

import java.util.LinkedList;

import lejos.hardware.Button	;
import lejos.robotics.RegulatedMotor;



public class Hardware {
    private boolean init;
    private boolean buttonHold;
    private EV3LargeRegulatedMotor motLeft, motRight, servo;
    private Sensors sensors;
    private LinkedList<Integer> orientationHistory;
    private int maxOrHistorySize = 10;

    //in ms, delay between reading the senors
    // preferred value is 20 ms
    private final int sensorReadDelay = 20;

    /** these are default values, they should be modified in Line, Labyrinth and ..**/
    private int motorMaxSpeedProcentage = 60;
    //default value is 6000
    private int motorAccelaration = 6000;

    private int turnSpeedProcentage = 40;
    //050 is too much swings back and fort, 25 is okay, just stop, 40 is also all right

    //eveyrhing over high is white
    private float midPointBWHigh = (float) 0.28;
    private float midPointBWLow = (float) 0.11;

    private float midPointRedHigh = (float) 0.2;
    private float midPointRedLow = (float) 0.1;

    public final CColor red = new CColor(0.2937f, 0.08f, 0.025f);
    public final CColor dRed = new CColor(0.339f, 0.087f, 0.032f);
    public final CColor blue = new CColor(0.050f, 0.17f, 0.13f);
    public final CColor white = new CColor(0.296f, 0.474f, 0.232f);
    public final CColor black = new CColor(0.054f, 0.091f, 0.028f);
    public final CColor brown = new CColor(0.05f, 0.06f, 0.025f);
    public final CColor trueblack = new CColor(0.06f, 0.013f, 0.01f);


    public final CColor redwhite = new CColor(0.304f, 0.319f, 0.142f);
    public final CColor blackwhite = new CColor(0.136f, 0.211f, 0.067f);
    public final CColor bluewhite = new CColor ( 0.105f, 0.235f, 0.148f);
    public final CColor blueblack = new CColor(0.165f, 0.262f, 0.119f);
    public final CColor newBlue = new CColor(0.018f, 0.070f, 0.063f);
    public final CColor nothing = new CColor(0.0f, 0.00098f, 0.00098f);



    //To let us know if we have to correct to red or white
    public enum actualColor{
    	BW, RB;
    }
    public actualColor acColor;
    
    public Hardware() throws IllegalArgumentException{
	    //copy the values after first calibration
	    led(9);
	    
	    System.out.println("Hardware is being initialized...");
	
	    System.out.println("Checking motor right...");
	    motRight = new EV3LargeRegulatedMotor(MotorPort.D);
	    System.out.println("Done!");
	
	    System.out.println("Checking motor left...");
	    motLeft = new EV3LargeRegulatedMotor(MotorPort.A);
	    System.out.println("Done!");
	
	    System.out.println("Checking servo...");
	    servo = new EV3LargeRegulatedMotor(MotorPort.B);
	    System.out.println("Done!");
	
	    System.out.println("Checking touch sensor...");
	    EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S3);
	    System.out.println("Done!");
	    
	    System.out.println("Checking color sensor...");
	    EV3ColorSensor color = new EV3ColorSensor(SensorPort.S4);
	    System.out.println("Done!");
	    
	    System.out.println("Checking ultrasonic sensor...");
	    EV3UltrasonicSensor ultraSensor = new EV3UltrasonicSensor(SensorPort.S2);
	    System.out.println("Done!");


	    System.out.println("Checking gyro sensor...");
	    EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S1);
	    System.out.println("Done!");


	    System.out.println("Initialising the wrappers...");
	    System.out.println("touch wrapper...");
	    SingleValueSensorWrapper touch = new SingleValueSensorWrapper(touchSensor, "Touch");
	    System.out.println("Done!");
	    
	    System.out.println("color wrapper...");
	    SingleValueSensorWrapper col = new SingleValueSensorWrapper(color, "RGB");
	    System.out.println("Done!");
	    
	    System.out.println("ultrasonic wrapper...");
	    SingleValueSensorWrapper dist = new SingleValueSensorWrapper(ultraSensor, "Distance");
	    System.out.println("Done!");
	    

	    System.out.println("gyro wrapper...");
	    SingleValueSensorWrapper gyro = new SingleValueSensorWrapper(gyroSensor, "Angle");
	    System.out.println("Done!");

	    
	    sensors = new Sensors(sensorReadDelay, touch, col, dist, gyro);

        initialize();

	    if(!init){
	        System.out.println("WARNING: Hardware not initialized properly");
	    }

	    led(1);

        Sound.setVolume(20);
	    orientationHistory = new LinkedList<>();
    }

    /**
     *
     * @return true, if we found the beacon
     */
    public boolean foundBeacon(LinkedList<CColor> beaconList) {
        if(beaconList == null){
            System.out.println("beaconList is null");
            return false;
        }

        CColor current = readRGBColor();

        for ( CColor colorIt : beaconList){
            if (colorIt.equalsTolerance(current)){
                System.out.println("Beacon found!");
                return true;
            }
        }

        return false;
    }


    /**
     * initializes the sensors
     */
    public void startSensors() {
        System.out.println("Starting sensors..");

        if(! sensors.isInit()){
            System.out.println("Cannot start, sensors must be initialized!");
            return;
        }

        sensors.start();
        System.out.println("Sensors are started");
    }

    /**
     * make a sound
     */
    public void beep() {
        Sound.beep();
    }

    public float readColorIntensity() {
        return sensors.colorIntensity();
    }


    public CColor readRGBColor() {
    	return sensors.colorRGB();
    }

    /**
     * get distance from the ultrasonic sensor
     * @return the distance in CM
     */
    public int getDistance() {

        return Math.round(sensors.getDistance()*100);
    }

    public int getMotorAngle() {
        return motRight.getTachoCount();
    }

    public void setMotorMaxSpeedProcentage(int motorMaxSpeedProcentage) {
        this.motorMaxSpeedProcentage = motorMaxSpeedProcentage;
    }

    public void setMotorAccelaration(int motorAccelaration) {
        this.motorAccelaration = motorAccelaration;
    }

    public void setTurnSpeedProcentage(int turnSpeedProcentage) {
        this.turnSpeedProcentage = turnSpeedProcentage;
    }

    public void robotTurnBlock(int angle) {
            mySleep(50);
            motorsWaitStopMoving();

            int absoluteAngle = angle * 6;

            motorSetSpeedProcentage(turnSpeedProcentage);

            synchMotors();

            if(angle < 0){
                motLeft.rotate(absoluteAngle, true);
                motRight.rotate(-absoluteAngle, true);
            }else{
                motRight.rotate(-absoluteAngle, true);
                motLeft.rotate(absoluteAngle, true);
            }

            deSynchMotors();

            motorsWaitStopMoving();
    }

    public boolean isRightUp() {
        return Button.LEFT.isUp();
    }


    public enum ButtonType {
        UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE, NONE
    }

    /**
     * DEBUGGING
     *  which button is pressed
      * @return  UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE or NONE
     */
    public ButtonType getButtonType(){
        ButtonType buttonType = ButtonType.NONE;
        int clickedButton = Button.getButtons();
        
        if(clickedButton != 0) {
        	if(!buttonHold) {
		        switch (clickedButton){
		            case Button.ID_UP:
		                buttonType = ButtonType.UP;
		                buttonType = ButtonType.ENTER;
		                break;
		            case Button.ID_DOWN:
		                buttonType = ButtonType.DOWN;
		                break;
		            case Button.ID_LEFT:
		                buttonType = ButtonType.LEFT;
		                break;
		            case Button.ID_RIGHT:
		                buttonType = ButtonType.RIGHT;
		                break;
		            case Button.ID_ENTER:
		                buttonType = ButtonType.ENTER;
		                break;
		            case Button.ID_ESCAPE:
		                buttonType = ButtonType.ESCAPE;
		        }
		        buttonHold = true;
        	}
        } else {
        	buttonHold = false;
        }
        return buttonType;
    }

    public boolean isInit() {
        return init;
    }

    /**
     * initalizes the motors and the sensors
     * @return true if everything is ok
     */
    private boolean initialize(){
        System.out.println("Hardware is being initialized");

        if(motLeft == null){
            System.out.println("WARNING: Left motor is null!");
            return false;
        }

        if(motRight == null){
            System.out.println("WARNING: Right motor is null!");
            return false;
        }

        int motorAbsoluteSpeed = (int) (motLeft.getMaxSpeed() * motorMaxSpeedProcentage / 100 );
        motRight.setSpeed(motorAbsoluteSpeed);
        motLeft.setSpeed(motorAbsoluteSpeed);

        motRight.setAcceleration(motorAccelaration);
        motLeft.setAcceleration(motorAccelaration);

        init = sensors.initialize() ? true : false;

        
        return init;
    }

    /**
     * /*
     0: turn off button lights
     1/2/3: static light green/red/yellow
     4/5/6: normal blinking light green/red/yellow
     7/8/9: fast blinking light green/red/yellow
     >9: sam as 9.
     * @param color
     */
    public void led(int color){
        Button.LEDPattern(color);
    }

    /**
     *
     * @return true, if the touch sensor is pressed
     */
    public boolean isTouchPressed(){

        if (Float.compare(sensors.touch(), (float)0) > 0){
            System.out.println("Touch is pressed, yes");
            beep();
            return true;
        }

        return false;
    }

    /**
     * both motors move an angle forward;
     * function blocks until the movement is done!
     *
     */
    public void motorForward(int angle){
        synchMotors();

        motRight.rotate(angle, true);
        motLeft.rotate(angle); //in case this works automatic with the first motor

        deSynchMotors();
    }

    public void motorForwardBlock(int angle){
        motorsWaitStopMoving();
        mySleep(50);

        synchMotors();

        motRight.rotate(angle, true);
        motLeft.rotate(angle);

        deSynchMotors();

        motorsWaitStopMoving();
    }



    /**
     * stopping both motors
     */
    public void motorStop(){
        motLeft.stop(true);
        motRight.stop(true);
    }

    /**
     * sets the speed ot the motor to a procentage of the maximum speed
     * @param procentage; must be between 0 and 1
     */
    public void motorSetSpeedProcentage(int procentage){

       // System.out.println("Setting speed to " + procentage + " procent");
        int motorAbsoluteSpeed = Math.round (motLeft.getMaxSpeed() * procentage / 100 );
        motRight.setSpeed(motorAbsoluteSpeed);
        motLeft.setSpeed(motorAbsoluteSpeed);
    }

    /**
     * syncing motors, so that they move the same amount of degrees
     */
    private void synchMotors(){
        //adding left motor to a an array and synchronizing with the right
        motRight.synchronizeWith(new RegulatedMotor[] {motLeft});
        motRight.startSynchronization();
    }

    private void deSynchMotors(){
        motRight.endSynchronization();
    }

    /**
     * motor turns
     * @param angle (can also be negative);
     * negative means go left, positive means go right
     */
    public void robotTurn(int angle){

        //return;

        //360 * (2 * pi) / ( (1/4) *2*pi*r1)
        int absoluteAngle = angle * 6;

        //motorsWaitStopMoving();
        motorSetSpeedProcentage(turnSpeedProcentage);

        synchMotors();

        motRight.rotate(-absoluteAngle, true);
        motLeft.rotate(absoluteAngle, true);


        deSynchMotors();

    }
    
    /**
     *
     * @return current angle, read from the gyro sensor
     */
    public int getAngle(){
        return Math.round(-sensors.getAngle());
        //return 0;
    }

    public void robotTurnNonBlockOneWheel(int angle){

        int absoluteAngle = angle * 12;

        motorsWaitStopMoving();

        synchMotors();

        if(angle < 0){
            motRight.rotate((int) Math.abs(absoluteAngle*1.2), true);
        }else{
            motLeft.rotate((int) Math.round(absoluteAngle), true);
        }

        deSynchMotors();
    }

    /**
     * functions blocks until the motors have stopped turning
     */
    public void motorsWaitStopMoving(){

        while(motLeft.isMoving() && motRight.isMoving()){
           mySleep(5);
        }

    }

    public boolean motorsAreMoving(){
        if(! motRight.isMoving() && ! motLeft.isMoving() ){
            return false;
        }

        return true;
    }

    /**
     *
     * @return true, if the colorIntensity sensor is on white; works with check with the midpoint
     */
    public boolean isOnWhite(){

        CColor current = readRGBColor();
        return current.equalsTolerance(white);

    }


    public boolean isOnBlack() {
        return readRGBColor().equalsTolerance(black);
    }
    
    /**
    *
    * @return true, if the colorIntensity sensor is on red
    * ; works with check with the midpoint
    */
   public boolean isOnRed(){
       CColor current = readRGBColor();
       return current.equalsTolerance(red);
   }
    

    /**
     *
     * @return the midPoint;
     * DO NOT USE to check if sensor is on white -> isOnWhite() function
     */
    public float getMidPointBW(){
        return (white.getIntensity() + black.getIntensity())/2 + black.getIntensity();
    }

    /**
    *
    * @return the midPoint;
    * DO NOT USE to check if sensor is on white -> isOnWhite() function
    */
   public float getMidPointRed(){
       return 0.15f;
       //return (red.getRed()/2 + black.getRed())/2 + black.getRed();
   }
    
    /**
     *
     * @return true if the sensor is on the midpoint between black and white
     */
    public boolean isOnMidpointBW(){
        if(sensors.colorIntensity() < midPointBWHigh && sensors.colorIntensity() > midPointBWLow){
        	//updateOrientation();
            System.out.println("I am on the middle BW");
            acColor = actualColor.BW;
            return true;
        }
        
        return false;
    }

    public boolean isOnMidpointRed(){
        CColor current = readRGBColor();

        if(current.getRed() > midPointRedLow && current.getRed() < midPointRedHigh ){
            return true;
        }

        return false;
    }




    public void servoGoUp(){
        servo.rotate(-90);
        //servo.flt();
    }

    public void servoGoDown(){

        servo.rotate(85);
        //mySleep(50);
        //servo.flt();
    }

    public boolean isEscapeUp(){
        return Button.ESCAPE.isUp();
    }

    public boolean isUpUp(){
        return Button.UP.isUp();
    }

    public boolean isLeftUp() { return Button.LEFT.isUp(); }





    private void mySleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    private void updateOrientation(){
    	
    	int resetTolerance = 40;
    	if(orientationHistory.isEmpty()){
            System.out.println("it is empty, so we add, no conditions");
           // orientationHistory.add(getAngle());
    	} else {
    		int average = 0;
    		//int angle = getAngle();
    		for(float value : orientationHistory){
    			average += value;
    		}
    		average /= orientationHistory.size();
            //%TODO: Turn avarge-angle to int or compare with Float.compare
    		if(  (Math.abs(average-angle) ) > resetTolerance){
                System.out.println("differnce is above tolerance, clear the list");
                orientationHistory.clear();
    		}
    		orientationHistory.add(angle);
    		if(orientationHistory.size() > maxOrHistorySize){
    			orientationHistory.removeLast();
    		}
    	}

       // System.out.println("after update orientation " + orientationHistory);
    }*/



    /**
     * 
     * @return -1 if too less measurepoints are available. Otherwise eastimate an angle.
     */
    public int estimateOrientation(){
        //System.out.println("Last values: " + orientationHistory);

    	if(orientationHistory.size() < 2){
    		return -1;
    	}
    	float average = 0.f;
		for(float value : orientationHistory){
			average += value;
		}
		average /= orientationHistory.size();
		return Math.round(average);
    }
    
    public void rotateRightMotor(int angle) {
    	   motRight.rotate(angle, true);
    }


    public void rotateRightMotorBlock(int angle) {
        motorsWaitStopMoving();
        mySleep(50);
        motorSetSpeedProcentage(turnSpeedProcentage);
 	    motRight.rotate(angle);
    }
    
    public void rotateLeftMotor(int angle) {
    		motLeft.rotate(angle, true);
    }
    
    public void rotateLeftMotorBlock(int angle) {
        motorsWaitStopMoving();
        mySleep(50);
        motorSetSpeedProcentage(turnSpeedProcentage);
        motLeft.rotate(angle);
    }
}
