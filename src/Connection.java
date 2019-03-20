import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import java.util.Arrays;

/**
 * Created by Kimberly Orr.
 * Class to organize and initialize information associated with each connection.
 */
public class Connection {
    private boolean isEnd;
    private int connectionNum;
    private double time;
    private int srcNode;
    private int destNode;
    private int bandwidth;
    private int[] slotsUsed;
    private int[] path;
    private Connection other; //TODO: Maybe just make this Connection start???

    // Constructor

    public Connection(){
    }

    // Class Methods

    /**
     * Calculate the time of this Connection based on a previous time and specified interarrival time.
     * @param prevTime The previous connection's time
     * @param interarrival The interarrival time to use (based on Exponential Distribution defined in TrafficGenerator)
     */
    public void calcTime(double prevTime, double interarrival){
        this.setTime(prevTime + interarrival);
    }

    /**
     * Pick the Source and Destination nodes for this Connection.
     * @param numNodes The number of nodes in the physical network.
     */
    public void pickSrcAndDest(int numNodes){
        UniformIntegerDistribution uni = new UniformIntegerDistribution(0, numNodes-1);
        this.setSrcNode(uni.sample());
        do {
            this.setDestNode(uni.sample());
        } while(this.getDestNode() == this.getSrcNode());
    }

    /**
     * For debugging.
     * @param src
     * @param dest
     */
    public void pickSrcAndDestDebug(int src, int dest){
        this.setSrcNode(src);
        this.setDestNode(dest);
    }

    /**
     * Mechanism to release resources previously in use by this connection.
     * Assumes that the path is valid in the network.
     * @param ntwk
     */
    public void releaseResources(PhysicalNetwork ntwk){
        Edge[][] edges = ntwk.getNetwork();
        for(int i=1; i < path.length; i++){
            edges[path[i-1]][path[i]].markRangeFree(slotsUsed);
            edges[path[i]][path[i-1]].markRangeFree(slotsUsed);
        }
    }

    /**
     * Mechanism to claim resources in use by this connection.
     * Assumes that the path is valid in the network.
     * Only to be used after connection is routed!!
     * @param ntwk
     */
    public void claimResources(PhysicalNetwork ntwk){
        System.out.println("CR Path: " + Arrays.toString(path));
        Edge[][] edges = ntwk.getNetwork();
        if(path.length <= 1){
            System.err.println("Connection: claim: simply routing a connection to itself");
        }
        else {
            for (int i = 1; i < path.length; i++) {
                edges[path[i - 1]][path[i]].markRangeTaken(slotsUsed);
                edges[path[i]][path[i - 1]].markRangeTaken(slotsUsed);
            }
        }
    }

    // Getters and setters

    public boolean getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(boolean end) {
        isEnd = end;
    }

    public int getConnectionNum() {
        return connectionNum;
    }

    public void setConnectionNum(int connectionNum) {
        this.connectionNum = connectionNum;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
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

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int[] getSlotsUsed() {
        return slotsUsed;
    }

    public void setSlotsUsed(int[] slotsUsed) {
        this.slotsUsed = slotsUsed;
        this.other.slotsUsed = slotsUsed;
    }

    public int[] getPath() {
        return path;
    }

    public void setPath(int[] path) {
        this.path = new int[path.length];
        this.other.path = new int[path.length];
        for(int i=0; i<path.length; i++){
            this.path[i] = path[i];
            this.other.path[i] = path[i];
        }
    }

    public Connection getOther() {
        return other;
    }

    public void setOther(Connection other) {
        this.other = other;
    }

    /**
     * The toString for a Connection.
     * @return information about this Connection
     */
    public String toString(){
        String startOrEnd = "";
        if(isEnd)
            startOrEnd = "End";
        else
            startOrEnd = "Start";
        return startOrEnd + " " + this.connectionNum
                + "\nTime: " + this.time
                + "\nsrcNode, destNode: ("  + Arrays.toString(path); //+ this.srcNode + ", " + this.destNode + ")"
//                + "\n"; //+ Arrays.toString(slotsUsed);
                //")\nbandwidth: " + this.bandwidth;
    }

}
