public class Frame {
    private boolean free;
    private boolean dirty;
    private Process currentProcess;
    private int[] bits;

    public Frame(){
        this.free = true;
        this.dirty = false;
        this.bits = new int[8];
    }

    // process
    public void setCurrentProcess(Process p){
        this.currentProcess = p;
    }
    public Process getCurrentProcess(){
        return this.currentProcess;
    }

    // free
    public void setFree(boolean free){
        this.free = free;
    }
    public boolean isFree(){
        return this.free;
    }

    // dirty
    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }
    public boolean isDirty(){
        return this.dirty;
    }
}
