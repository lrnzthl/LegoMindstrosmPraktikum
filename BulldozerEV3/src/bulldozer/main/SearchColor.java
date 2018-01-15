package bulldozer.main;


public class SearchColor extends Brains{

	
    
    private final int step = 45;
    private int turningAngle = 10;
    

    private final float Kp = 1.5f;
    
    private boolean foundRed = false;
    private boolean foundWhite = false;
    
    // 1 for the last rotation to right, -1 for last rotation to left
    private int lastRotation = 1;
    
    
    private int expectedDistance;
    private int distanceTolerance = 3;
    
	public SearchColor(Hardware hardware){
        super(hardware);  
    }

	@Override
	public void run() {

	    /*
        int number = 100000000;
		hardware.servoGoDown();
        while(number>0){
            System.out.println("Angle is " + hardware.getAngle());
            System.out.println("color is " + hardware.readRGBColor());
            number--;
            mySleep(20);
        }*/
		
		hardware.motorForward(180);
		hardware.rotateRightMotor(5);
		hardware.motorForward(180);
		

		
		//The distance expected between the robot and the wall
		hardware.servoGoUp();
		expectedDistance = hardware.getDistance();
		System.out.println("The distance is : " + expectedDistance);
		
		while(!(foundRed && foundWhite)) {
			
			while(!hardware.isOnRed() && !hardware.isOnWhite()) {
				while(hardware.getDistance() > expectedDistance + distanceTolerance || hardware.getDistance() < expectedDistance - distanceTolerance){
					System.out.println("Error in the distance, correcting");
	                rotateToDistance();
	            }
				while(hardware.isTouchPressed()){
	                System.out.println("Touch is pressed, cannot go forward");
	                rotateInTheWall();
	                expectedDistance = hardware.getDistance();
	            }
				hardware.motorSetSpeedProcentage(30);
	            hardware.motorForward(step);
			}
			
			if (hardware.isOnRed() && !foundRed) {
				System.out.println("FOUND THE RED");
				foundRed = true;
				hardware.beep();
			}
			else if (hardware.isOnWhite() && !foundWhite) {
				System.out.println("FOUND THE WHITE");
				foundWhite = true;
				hardware.beep();
			}
		}
		
	}
	
	//Rotates if it´s getting further away or closer of the wall.
	public void rotateToDistance() {
		
		while(hardware.getDistance() > expectedDistance + distanceTolerance || hardware.getDistance() < expectedDistance - distanceTolerance){
			hardware.motorSetSpeedProcentage(5);
			if (hardware.getDistance() > expectedDistance ) {
				hardware.rotateRightMotor(5);
			}
			else {
				hardware.rotateLeftMotor(5);
			}
			
		}
	}
	
	
	//Rotates at the end of the wall, to the right or left depending on the lastRotation
	public void rotateInTheWall() {
		hardware.motorForwardBlock(-180);
		
	    System.out.println("Turning...");
		hardware.robotTurnBlock(lastRotation * -90);
        hardware.motorForward(180 + step);
        hardware.robotTurnBlock(lastRotation * -90);

        lastRotation *= -1;
    }
	
	
}
