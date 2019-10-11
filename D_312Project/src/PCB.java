import java.util.ArrayList;
import java.util.Random;

public class PCB {

    private int priority;
    private int requestedMemory;
    private CurrentState currentState;
    private int programCounter;
    private ArrayList<Integer> pages;
    private int timeUntilRun;

    public PCB() {
        Random rand = new Random();
        priority = rand.nextInt(500) + 1;
        requestedMemory = rand.nextInt(8) + 1;
        currentState = CurrentState.NEW;
        programCounter = 0;
        this.pages = new ArrayList<>();
        this.timeUntilRun = 0;
    }

    public int getRequestedMemory() {
        return this.requestedMemory;
    }

    // pages
    public ArrayList<Integer> getPages(){
        return this.pages;
    }
    public void setPages(ArrayList<Integer> pages){
        this.pages = pages;
    }

    // priority
    public int getPriority() {
        return this.priority;
    }
    public void setPriority(int priority) {
        this.priority = this.priority;
    }

    // state
    public CurrentState getCurrentState() {
        return this.currentState;
    }
    public void setCurrentState(CurrentState currentState) {
        this.currentState = currentState;
    }
    public boolean updateState(Instruction instruction){
        if(instruction == null){
            currentState = CurrentState.TERMINATED;
            return true;
        }else{
            currentState = instruction.getState();
            return false;
        }
    }

    // program counter
    public int getProgramCounter() {
        return this.programCounter;
    }
    public void updateProgramCounter() {
        this.programCounter++;
    }

    // time until running
    public void updateTimeUntilRun(){
        timeUntilRun = timeUntilRun + 1;
        if(timeUntilRun > 16){
            if(priority > 1) {
                priority = priority - 1;
            }
            timeUntilRun = 0;
        }
    }
    public int getTimeUntilRun(){
        return this.timeUntilRun;
    }

    @Override
    public String toString(){
        return "Priority: " + getPriority() + "   Requested Memory: " + getRequestedMemory() +
                "   CurrentState: " + getCurrentState() + "\n";
    }

}
