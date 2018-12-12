public class Edge {
    private boolean[] slots; // false if empty
    private int numFibers;
    private int distance;
    private int srcNode;
    private int destNode;
    //list of adjacent nodes?

    public Edge(int numSlots, int fibers, int dist, int src, int dest) {
        slots = new boolean[numSlots];
        numFibers = fibers;
        distance = dist;
        srcNode = src;
        destNode = dest;
    }

    public String toString(){
        String str = "";
        for(int i=0; i<slots.length; i++){
            str += i + ": " + slots[i] + "\n";
        }
        str += distance;
        return str;
    }

    /**
     * Checks to see if the requested slots are available in this edge.
     * @param start
     * @param end
     * @return true if available and false if not
     */
    public boolean isRangeFree(int start, int end){
        boolean free = true;
        if(end >= slots.length || start < 0){
            System.err.println("Error: Requesting slots that don't exist");
        }
        else{
            for(int i=start; i<=end; i++){
                if(slots[i] == true){
                    free = false;
                }
            }
        }
        return free;
    }

    /**
     * Marks slots along the edge as in use.
     * @param start
     * @param end
     */
    public void markRangeTaken(int start, int end){
        if(end >= slots.length){
            System.err.println("Marking slots that don't exist");
        }
        for(int i=start; i<=end; i++){
            slots[i] = true;
        }
    }

    /**
     * Marks slots along the edge as free.
     * @param start
     * @param end
     */
    public void markRangeFree(int start, int end){
        if(end >= slots.length){
            System.err.println("Marking slots that don't exist");
        }
        for(int i=start; i<=end; i++){
            slots[i] = false;
        }
    }

    /**
     *  Getters and Setters
     */
    public boolean[] getSlots() {
        return slots;
    }

    public void setSlots(boolean[] slots) {
        this.slots = slots;
    }

    public int getNumFibers() {
        return numFibers;
    }

    public void setNumFibers(int numFibers) {
        this.numFibers = numFibers;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(int srcNode) {
        this.srcNode = srcNode;
    }

    public int getDestNode() {
        return destNode;
    }

    public void setDestNode(int destNode) {
        this.destNode = destNode;
    }
}
