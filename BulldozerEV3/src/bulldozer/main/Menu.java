package bulldozer.main;




public class Menu {

    private ParcourState state;
    private ParcourState selectedState;
    private Hardware hardware;

    private Brains brain;


    public Menu(Hardware hardware){
        //beginning state is always menu
        state = ParcourState.MENU;
        selectedState = ParcourState.values()[1];

        //checking if the hardware is initisalized properly
        if(hardware != null || hardware.isInit() ){
            this.hardware = hardware;
        }


    }

    private void setState(ParcourState state){
        if (state != null){
            System.out.println("Changing state to " + state);
            this.state = state;
        }
    }

    public ParcourState getState() {
        return state;
    }

    public void start(){
        boolean running = true;

        while (running) {
            showOptions();
            int newSelectedState;
            switch (hardware.getButtonType()) {
                case LEFT:
                	newSelectedState = selectedState.getId() - 1;
                    
                    if (newSelectedState < 1) {
                        newSelectedState = ParcourState.values().length - 1;
                    }
                    selectedState = ParcourState.values()[newSelectedState];
                    break;
                case RIGHT:
                    newSelectedState = selectedState.getId() + 1;

                    if (newSelectedState > ParcourState.values().length - 1) {
                        newSelectedState = 1;
                    }
                    selectedState = ParcourState.values()[newSelectedState];
                    break;
                case ENTER:
                    state = selectedState;
                    startBrain();
                    break;
                case ESCAPE:
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void startBrain() {
        switch (state){
            case LINE:
                brain = new Line(hardware);
                break;
            case LABYRINTH:
                brain = new Labyrinth(hardware);
                break;
            case BARRIER:
                //
                break;
            case BRIDGE:
                //
                break;
         //   case FINDCOLOUR:
                //
          //      break;
            default:
                System.out.println("Cannot start MENU state");
                break;
        }

        int returnState = brain.mainLoop();
        switch (returnState){
        case -1:
        	state = ParcourState.MENU;
        	break;
        case 0:
        	break;
        case 1:
        	if(state.getId() < state.values().length - 1){
        		state = state.values()[state.getId() + 1];
        		selectedState = state;
        		startBrain();
        	} else {
        		state = ParcourState.MENU;
        		System.out.println("Completed last task :)");
        	}
        	break;
        default:
        	break;	
        }
    }

    private void showOptions(){
        for (ParcourState s : ParcourState.values()){
            if (s.equals(ParcourState.MENU)){
                continue;
            }
            
            if(s.equals(this.selectedState)){
                System.out.print("[" + s.name() + "]");
            }else{
                System.out.print(s.name());
            }

            System.out.print("  ");
        }
        System.out.println();
    }
}