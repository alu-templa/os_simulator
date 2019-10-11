import java.util.ArrayList;
import java.util.Random;

public class Process {
    ArrayList<Instruction> instructions;
    String name;
    private PCB pcb;
    private int ID;
    private int size;
    private Process pid;
    private ArrayList<Process> cid;

    public Process(int ID) {
        Random rand = new Random();
        int power = rand.nextInt(5) + 1;

        this.name = null;
        this.ID = ID;
        this.instructions = new ArrayList<>();
        this.pcb = new PCB();
        this.size = (int)Math.pow(2, power); // up to 64
        this.cid = new ArrayList<>();
    }

    // multithreading
    public void setPid(Process pid){
        this.pid = pid;
    }
    public Process getPid(){
        return this.pid;
    }
    public ArrayList<Process> getCid(){
        return this.cid;
    }
    public void reapCid(int reapIndex){
        cid.get(reapIndex).kill();
    }
    public void kill(){
        for(int i = 0; i < getCid().size(); i++){
            reapCid(i);
        }
        pcb.setCurrentState(CurrentState.TERMINATED);
    }
    public void fork(){
        Random rand = new Random();
        Process process = new Process(Main.globalPid++);
        Main.globalPid++;

        int priority = rand.nextInt(500) + 1;
        int power = rand.nextInt(5) + 1;
        int size = (int)Math.pow(2, power); // up to 64
        int numberOfInstructions = rand.nextInt(5) + 1;

        process.setPriority(priority);
        process.setSize(size);

        for(int i = 0; i < numberOfInstructions; i++){
            int instructionType = rand.nextInt(3);
            int instructionTicks = rand.nextInt(25) + 1;

            if(instructionType == 0) {
                process.addInstruction(new Instruction(InstructionEnum.COMPUTE, instructionTicks, false));
            }else if(instructionType == 1) {
                process.addInstruction(new Instruction(InstructionEnum.IO, instructionTicks, false));
            }else if(instructionType == 2){
                    process.addInstruction(new Instruction(InstructionEnum.YIELD, instructionTicks, false));
            }
        }
        process.setPid(this);
        cid.add(process);
    }

    // ID
    public void setID(int ID){
        this.ID = ID;
    }
    public int getId(){
        return this.ID;
    }

    // pcb
    public PCB getPcb() {
        return this.pcb;
    }

    // name
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    // priority
    public void setPriority(int priority){
        pcb.setPriority(priority);
    }
    public int getPriority(){
        return pcb.getPriority();
    }

    // memory
    public int getRequestedMemory(){
        return this.pcb.getRequestedMemory();
    }
    public ArrayList<Integer> getPages(){
        return this.pcb.getPages();
    }

    // size
    public void setSize(int size){
        this.size = size;
    }
    public int getSize(){
        return this.size;
    }

    // pcb wrappers
    public CurrentState getCurrentState(){
        return this.getPcb().getCurrentState();
    }
    public int getProgramCounter(){
        return this.getPcb().getProgramCounter();
    }

    // instructions
    public void addInstruction(Instruction instruction) {
        this.instructions.add(instruction);
    }
    public Instruction getCurrentInstruction() {
        if(pcb.getProgramCounter() >= this.instructions.size()) {
            return null;
        } else {
            return this.instructions.get(pcb.getProgramCounter());
        }
    }
    public boolean finishedInstruction() {
        return (this.getCurrentInstruction().getInstructionTicks() < 1);
    }
    public void setCurrentInstruction(int currentInstruction) {
        if(currentInstruction > this.instructions.size()) {
            System.out.println("Error at setCurrentInstruction");
        } else if (currentInstruction == this.instructions.size()) {
            pcb.setCurrentState(CurrentState.TERMINATED);
        } else {
            pcb.updateProgramCounter();
        }
    }
    public InstructionEnum getInstructionType(){
        Instruction instruction = getCurrentInstruction();
        if(instruction != null){
            return instruction.getInstructionEnum();
        } else {
            return null;
        }
    }
    public String getIsCritical(){
        Instruction instruction = getCurrentInstruction();
        if(instruction != null){
            return instruction.getIsCriticalSection();
        } else {
            return null;
        }
    }

    // program counter
    public void processUpdateProgramCounter() {
        getPcb().updateProgramCounter();
        boolean finished = getPcb().updateState(getCurrentInstruction());
        if(finished){ kill(); }
    }

    // run
    public void setProcessToRun(){
        this.pcb.setCurrentState(CurrentState.RUNNING);
    }
    public void run(){
        Instruction instruction = this.getCurrentInstruction();
        if(instruction != null && instruction.getInstructionTicks() > 0) {
            instruction.tick();
        }
    }

    @Override
    public String toString(){
        return name + " " + "   Size: " + getSize() + "   Instruction: " + getProgramCounter() +
                "   InstructionType: " + getInstructionType() + "   IsCritical: " + getIsCritical() + "   " + getPcb();
    }
}