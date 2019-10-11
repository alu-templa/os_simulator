public class CPU {
    private String cpuName;
    private MMU mmu;
    private Scheduler rs;
    private int timeQuantum;
    private CoreSync coreSync1;
    private CoreSync coreSync2;
    private CoreSync coreSync3;
    private CoreSync coreSync4;
    private Thread thread1;
    private Thread thread2;
    private Thread thread3;
    private Thread thread4;

    public CPU(MMU mmu, int timeQuantum, String cpuName) {
        this.cpuName = cpuName;
        this.mmu = mmu;
        this.rs = new Scheduler(mmu);
        this.timeQuantum = timeQuantum;
        coreSync1 = new CoreSync(mmu, rs, timeQuantum);
        coreSync2 = new CoreSync(mmu, rs, timeQuantum);
        coreSync3 = new CoreSync(mmu, rs, timeQuantum);
        coreSync4 = new CoreSync(mmu, rs, timeQuantum);
        thread1 = new Thread(coreSync1);
        thread2 = new Thread(coreSync2);
        thread3 = new Thread(coreSync3);
        thread4 = new Thread(coreSync4);
    }

    public void run(){
        try{
            coreHandler(coreSync1, thread1);
            coreHandler(coreSync2, thread2);
            coreHandler(coreSync3, thread3);
            coreHandler(coreSync4, thread4);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addProcess(Process p) {
        this.rs.addProcess(p);
        for(Process cid : p.getCid()){
            addProcess(cid);
        }
    }

    public void coreHandler(CoreSync coreSync, Thread thread){
        if(coreSync.isDone()){
            thread.interrupt();
        } else {
            thread.run();
        }
    }

    public boolean cpuStillRunning(){
        return !(rs.isEmpty() && coreSync1.isDone() && coreSync2.isDone() && coreSync3.isDone() && coreSync4.isDone());
    }

    public String getCpuName(){
        return this.cpuName;
    }

}
