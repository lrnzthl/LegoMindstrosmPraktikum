package bulldozer.main;

public class Labyrinth extends Brains {


    public Labyrinth(Hardware hardware){
        super(hardware);
    }

    @Override
    protected void doLogic() {
        System.out.println("I .. Line Brains");

        while( ! hardware.isTouchPressed()){

            if(hardware.isOnWhite()){
                hardware.beep();
            }

            hardware.motorForwardBlock(360);
        }

        mySleep(500);

        System.out.println("Going again!");

        while( ! hardware.isTouchPressed()){
            hardware.motorForwardBlock(360);
        }
    }


    private void goingUntilWhiteLineIsLost(){
        //start going
        System.out.println("...going forward");
        hardware.motorForward();

        while(hardware.isOnWhite()){
            mySleep(100);
        }

        System.out.println("I don't see the white line");

        //robot is no more on the white line
        hardware.motorStop();
    }




}
