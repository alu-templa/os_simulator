import java.util.PriorityQueue;
import java.util.Comparator;

public class Scheduler { // Uses Round Robbin scheduling algorithm
    private PriorityQueue<Process> processReadyQueue;
    private PriorityQueue<Process> processIoQueue;
    private PriorityQueue<Process> processWaitingQueue;
    private PriorityQueue<Process> processTerminatedQueue;


    private MMU mmu;

    public Scheduler(MMU mmu){
        this.processReadyQueue =
                new PriorityQueue<>(25, new ProcessPriorityComparator());
        this.processIoQueue =
                new PriorityQueue<>(25, new ProcessPriorityComparator());
        this.processWaitingQueue =
                new PriorityQueue<>(25, new ProcessPriorityComparator());
        this.processTerminatedQueue =
                new PriorityQueue<>(25, new ProcessPriorityComparator());
        this.mmu = mmu;
    }

    // queue getters
    public PriorityQueue<Process> getProcessReadyQueue(){
        return processReadyQueue;
    }
    public PriorityQueue<Process> getProcessIoQueue() {
        return processIoQueue;
    }
    public PriorityQueue<Process> getProcessTerminatedQueue() {
        return processTerminatedQueue;
    }

    // queue ticking
    private void tickQueue(PriorityQueue<Process> queue){
        for(Process p : queue){
            p.getPcb().updateTimeUntilRun();
        }
    }
    public void tickWaitingProcessReadyQueue(){
        tickQueue(processReadyQueue);
    }
    public void tickWaitingProcessIoQueue(){
        tickQueue(processIoQueue);
    }

    // poll queue
    public Process pollProcessReadyQueue(){
        return processReadyQueue.poll();
    }
    public Process pollProcessIoQueue(){
        return processIoQueue.poll();
    }

    public void removeTerminated(){
        for(Process p : processTerminatedQueue){
            mmu.freeTheCache(p);
            mmu.freeTheMainMemory(p);
        }
        Process toAdd = processWaitingQueue.poll();
        if(toAdd != null){
            addProcess(toAdd);
        }
    }

    public void addProcess(Process p){
        if(p.getPcb().getCurrentState() == CurrentState.NEW){
            boolean allocated = mmu.allocateMemory(p);
            if(allocated){
                p.getPcb().setCurrentState(CurrentState.READY);
                addProcessToQueue(p);
            } else {
                this.processWaitingQueue.add(p);
            }
        } else {
            p.getPcb().updateState(p.getCurrentInstruction());
            addProcessToQueue(p);
        }
    }
    private void addProcessToQueue(Process p){
        if(p.getInstructionType() != null) {
            switch (p.getPcb().getCurrentState()){
                case READY:
                    processReadyQueue.add(p);
                    break;
                case WAITING:
                    processIoQueue.add(p);
                    break;
                case TERMINATED:
                    processTerminatedQueue.add(p);
            }
            p.getPcb().updateState(p.getCurrentInstruction());
        }
    }
    public boolean isEmpty(){
        return this.processReadyQueue.isEmpty() && this.processIoQueue.isEmpty();
    }
    private class ProcessPriorityComparator implements Comparator<Process> {
        @Override
        public int compare(Process p1, Process p2){
            return Integer.compare(p1.getPcb().getPriority(), p2.getPcb().getPriority());
        }
    }
}
