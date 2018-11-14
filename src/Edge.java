public class Edge {
    private int[] slots;
    private int distance;
    private int srcNode;
    private int destNode;
    //list of adjacent nodes?

    public Edge(int numSlots, int dist, int src, int dest) {
        slots = new int[numSlots];
        distance = dist;
        srcNode = src;
        destNode = dest;
    }

    public String toString(){
        String str = "";
        for(int i=0; i<slots.length; i++){
            str += i + ": " + slots[i] + "\n";
        }
        return str;
    }

    /**
     *  Getters and Setters
     */
    public int[] getSlots() {
        return slots;
    }

    public void setSlots(int[] slots) {
        this.slots = slots;
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
