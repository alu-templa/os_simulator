public class CoreSync implements Runnable{

    private boolean finishedRunning;
    private MMU mmu;
    private Scheduler rs;
    private int timeQuantum;
    private int currentTime;
    private Process runningProcess;
    private Process IoProcess;

    public CoreSync(MMU mmu, Scheduler rs, int timeQuantum){
        this.mmu = mmu;
        this.rs = rs;
        this.timeQuantum = timeQuantum;
        this.currentTime = 0;
        this.finishedRunning = false;
    }

    @Override
    public void run() {
        try {
            synchronized (this) {


                boolean finishedRunningProcess;
                boolean finishedIoProcess;
                boolean addedRunningProcess;
                boolean addedIoProcess;

                boolean runningIsNull = processIsNull(runningProcess);
                boolean ioIsNull = processIsNull(IoProcess);

                if (runningIsNull && ioIsNull && rs.isEmpty()) {
                    finishedRunning = true;
                    return;
                }
                if (runningIsNull) { // poll ready
                    runningProcess = rs.pollProcessReadyQueue();
                }
                if (ioIsNull) {  // poll IO
                    IoProcess = rs.pollProcessIoQueue();
                }

                if (!runningIsNull && runningProcess.getInstructionType() == InstructionEnum.RESOURCE) {
                    boolean allocatedResourses = mmu.requestResources(2);
                    if (!allocatedResourses) {
                        rs.addProcess(runningProcess);
                        runningProcess = null;
                    }
                }

                boolean timeClac;
                timeClac = timeCalculation(currentTime, timeQuantum);
                if(timeClac){
                    if (!runningIsNull) { // run ready
                        runningHandler(runningProcess, mmu, rs);
                    }
                    if (!ioIsNull) {  // deal with IO
                        ioHandler(IoProcess, mmu, rs);
                    }
                    // time quantom calculations
                    finishedRunningProcess = finishedProcess(runningProcess, rs);
                    if (finishedRunningProcess) {
                        runningProcess = null;
                    }
                    finishedIoProcess = finishedProcess(IoProcess, rs);
                    if (finishedIoProcess) {
                        IoProcess = null;
                    }

                    rs.removeTerminated();
                    currentTime++;
                } else {
                    addedRunningProcess = addProcessToScheduler(runningProcess, rs);
                    if (addedRunningProcess) {
                        runningProcess = null;
                    }
                    addedIoProcess = addProcessToScheduler(IoProcess, rs);
                    if (addedIoProcess) {
                        IoProcess = null;
                    }

                    currentTime = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDone() {
        return finishedRunning;
    }

    public boolean processIsNull(Process p){
        return (p == null);
    }

    private boolean timeCalculation(int currentTime, int timeQuantum) {
        return currentTime < timeQuantum;
    }

    private boolean finishedProcess(Process p, Scheduler rs) {
        boolean nullify = false;
        if (p != null && p.finishedInstruction()) {
            p.processUpdateProgramCounter();
            rs.addProcess(p);
            nullify = true;
        }
        return nullify;
    }

    private boolean addProcessToScheduler(Process p, Scheduler rs) {
        boolean nullify = false;
        if (p != null) {
            rs.addProcess(p);
            nullify = true;
        }
        return nullify;
    }

    // handlers
    private void pageHandler(Process p, MMU mmu) {
        p.getPcb().setPages(mmu.requestPages(p));
    }
    private void runningHandler(Process p, MMU mmu, Scheduler rs){
        p.setProcessToRun();
        pageHandler(p, mmu);
        p.run();
        rs.tickWaitingProcessReadyQueue();
    }
    private void ioHandler(Process p, MMU mmu, Scheduler rs){
        pageHandler(p, mmu);
        p.run();
        rs.tickWaitingProcessIoQueue();
    }
}
