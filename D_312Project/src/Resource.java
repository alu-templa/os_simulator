public class Resource {
    private boolean free;

    public Resource(){
        this.free = true;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free){
        this.free = free;
    }
}