public class Instruction {

    private InstructionEnum instructionEnum;
    private int instructionTicks;
    private boolean isCriticalSection;

    public Instruction(InstructionEnum instructionEnum){
        this.instructionEnum = instructionEnum;
        this.instructionTicks = 0;
        this.isCriticalSection = false;
    }
    public Instruction(InstructionEnum instructionEnum, int instructionTicks, boolean isCriticalSection){
        this.instructionEnum = instructionEnum;
        this.instructionTicks = instructionTicks;
        this.isCriticalSection = isCriticalSection;
    }

    // instructionTicks
    public int getInstructionTicks(){
        return this.instructionTicks;
    }
    public void setInstructionTicks(int instructionTicks){
        this.instructionTicks = instructionTicks;
    }

    // instructionEnum
    public InstructionEnum getInstructionEnum(){
        return this.instructionEnum;
    }
    public void setInstructionEnum(InstructionEnum instructionEnum){
        this.instructionEnum = instructionEnum;
    }

    public void tick(){
        this.instructionTicks = instructionTicks - 1;
    }

    public String getIsCriticalSection(){
        if(isCriticalSection){
            return "true";
        } else {
            return "false";
        }
    }

    public CurrentState getState() {
        if (instructionEnum == InstructionEnum.COMPUTE) {
            return CurrentState.READY;
        } else if (instructionEnum == InstructionEnum.IO) {
            return CurrentState.WAITING;
        } else if (instructionEnum == InstructionEnum.YIELD) {
            return CurrentState.READY;
        } else if (instructionEnum == InstructionEnum.RESOURCE){
            return CurrentState.READY;
        }else {
                return CurrentState.TERMINATED;
        }
    }
    @Override
    public String toString(){
        return instructionEnum + " " + instructionTicks;
    }
}