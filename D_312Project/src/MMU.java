import java.util.ArrayList;

public class MMU {
    private Frame[] mainMemory;
    private Frame[] secondaryMemory;
    private Frame[] cache;
    private int[] registers;
    private ArrayList<Resource> resources;

    public MMU() {
        this.mainMemory = new Frame[128];
        this.secondaryMemory = new Frame[256];
        this.cache = new Frame[2];
        registers = new int[16]; //1 x 16 MB
        this.resources = new ArrayList<>();

        // 128 x 8 MB frames = 1024 MB
        for (int i = 0; i < 128; i++) {
            mainMemory[i] = new Frame();
        }

        // 256 x 8 MB frames = 2048 MB
        for (int i = 0; i < 256; i++) {
            secondaryMemory[i] = new Frame();
        }

        // 2 x 8 MB frames = 16 MB
        for (int i = 0; i < 2; i++) {
            cache[i] = new Frame();
        }
    }

    public ArrayList<Integer> requestPages(Process p) {
        int requestedMemory = p.getRequestedMemory();
        ArrayList<Integer> indexedFrames = new ArrayList<>();

        while (requestedMemory > 0) {
            if (casheFreeSpace()) {
                // uses cache if able
                requestedMemory = requestedMemory - addToCache();
            } else {
                for (int i = 0; i < mainMemory.length; i++) {
                    if (mainMemory[i].isFree()) {
                        // uses free memory, sets to dirty
                        indexedFrames.add(i);
                        mainMemory[i].setDirty(true);
                        requestedMemory = requestedMemory - 1;
                    } else if (!mainMemory[i].isDirty()) {
                        // sets a used frame as dirty
                        indexedFrames.add(i);
                        mainMemory[i].setDirty(true);
                    } else {
                        // "cleans" frame, ready for reaping
                        mainMemory[i].setDirty(false);
                    }
                }
            }
        }
        freeFrames(secondaryMemory, indexedFrames);
        return indexedFrames;
    }

    public boolean allocateMemory(Process p) {
        int requestedMemory = p.getRequestedMemory();
        ArrayList<Integer> indexedFrames = new ArrayList<>();

        addToIndexedFrames(secondaryMemory, indexedFrames, requestedMemory);

        if (p.getRequestedMemory() == indexedFrames.size()) {
            ArrayList<Integer> neededFrames = new ArrayList<>();
            for (Integer i : indexedFrames) {
                neededFrames.add(i);
                secondaryMemory[i].setFree(false);
            }
            p.getPcb().setPages(neededFrames);
            return true;
        } else {
            return false;
        }
    }

    public void freeTheCache(Process p){
        for(Integer i : p.getPcb().getPages()){
            freeCache(i);
        }
    }

    public void freeTheMainMemory(Process p){
        freeFrames(mainMemory, p.getPages());
    }
    public void freeFrames(Frame[] memory, ArrayList<Integer> indexedMemeory) {
        for (Integer i : indexedMemeory) {
            if (!memory[i].isDirty()) {
                // mark indexed frames as dirty
                memory[i].setDirty(true);
            } else {
                // "cleans" frames
                memory[i].setFree(true);
                memory[i].setDirty(false);
            }
        }
    }
    public void freeCache(int indexedMemory) {
        if (indexedMemory < cache.length) {
            if (!cache[indexedMemory].isDirty()) {
                // mark indexed frames as dirty
                cache[indexedMemory].setDirty(true);
            } else {
                // "cleans" frames
                cache[indexedMemory].setFree(true);
                cache[indexedMemory].setDirty(false);
                registers[indexedMemory] = 0;
            }
        }
    }

    // cache & register
    public int addToCache(){
        int added = 0;
        for(int i = 0; i < cache.length; i++){
            if(cache[i].isFree()){
                cache[i].setFree(false);
                registers[i] = -1;
                added = added + 1;
            }
        }
        return  added;
    }
    public boolean casheFreeSpace(){
        boolean free = false;
        for(Frame f : cache){
            if(f.isFree()){
                free = true;
            }
        }
        return free;
    }

    // resources
    public boolean requestResources(int request){
        int avaliable = 0;
        int tracker = request;

        for(Resource resource : resources){
            if(resource.isFree()){
                // marks avaliable resources
                avaliable = avaliable + 1;
            }
        }
        if(avaliable > request){
            // sets resource as used for each resources if there are still reaquested resources
            for(Resource resource : resources){
                if(resource.isFree() && tracker > 0){
                    resource.setFree(false);
                    tracker = tracker - 1;
                }
            }
            return true;
        } else {
            for(Resource resource : resources){
                // frees unused resources
                resource.setFree(true);
            }
            return false;
        }
    }

    private void addToIndexedFrames(Frame[] memory, ArrayList<Integer> indexedFrames, int requestedMemory) {
        for (int i = 0; i < memory.length; i++) {
            // indexes free frames to be used until requestedMemory is 0
            if (memory[i].isFree()) {
                indexedFrames.add(i);
                requestedMemory = requestedMemory - 1;
            }
            if (requestedMemory == 0) {
                break;
            }
        }
    }

}
