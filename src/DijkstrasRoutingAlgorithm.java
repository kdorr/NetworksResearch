import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kimberly Orr in 2019.
 * Class to route connections using Dijkstra's shortest path algorithm and first fit slot assignment.
 */
public class DijkstrasRoutingAlgorithm {
    private PhysicalNetwork pn; //the physical network from the main program
    private int[] path; //the path determined by the algorithm
    private int[] slots; //the slots being used.
    private boolean[] isVisited; //true if visited, false if unvisited.
    private int[][] pathTable; //1st dimension: node, 2nd dimension: 0-distance, 1-predecessor

    public DijkstrasRoutingAlgorithm(){
        isVisited = new boolean[0];
    }

    public DijkstrasRoutingAlgorithm(PhysicalNetwork pn){
        this.pn = pn;
        isVisited = new boolean[pn.getNodeList().length];
        for(int i=0; i<pn.getNodeList().length; i++){
            isVisited[i] = false;
        }
    }

    /**
     * Function to be called by outside to route the traffic
     * @param src
     * @param dest
     */
    public boolean routeTraffic(int src, int dest, int bandwidth){
        determinePath(src, dest);
        if(determineSlots(bandwidth)){  //if the connection wasn't rejected.
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Helper method for determinePath. Finds each node's predecessor along the shortest path.
     * @param src The source node
     * @param dest The destination node
     */
    public void calcTable(int src, int dest){
        int numNodes = pn.getNodeList().length;
        this.pathTable = new int[numNodes][2];
        //initialization
        for(int i=0; i<numNodes; i++){
            pathTable[i][0] = Integer.MAX_VALUE;
            pathTable[i][1] = -98; //Arbitrary value to indicate error
        }
        pathTable[src][0] = 0; //distance = 0
        pathTable[src][1] = src; //predecessor = src

        int numVisited=0;
        boolean destVisited = false;
        do {
            //find the unvisited node with the least distance
            int minVal = Integer.MAX_VALUE;
            int min = -99; //minIndex
            for(int i=0; i<numNodes; i++){
                if(pathTable[i][0] < minVal && !isVisited[i]){
                    minVal = pathTable[i][0];
                    min = i;
                }
            }
            //calculate distances for all adjacent nodes
            for(int i=0; i<numNodes; i++){
                if(!isVisited[i] && pn.getNetwork()[min][i]!=null){  //Dijkstra's the other way should modify this
                        int newDistance = pn.getNetwork()[min][i].getDistance() + pathTable[min][0];
                        if (newDistance < pathTable[i][0]) {
                            pathTable[i][0] = newDistance;
                            pathTable[i][1] = min;
                        }
                }
            }
            isVisited[min] = true;

            //check to see if we can stop early
            if(min == dest){
                destVisited=true;
            }
        }while(numVisited<numNodes && !destVisited);
    }

    /**
     * Finds a path from the source node to the destination node using Dijkstra's shortest path algorithm.
     * @param src The source node
     * @param dest The destination node
     * @return an integer array containing the path
     */
    public void determinePath(int src, int dest){
        //calculate the table
        calcTable(src, dest);

        //once the table is calculated, get the path
        ArrayList<Integer> backwards = new ArrayList<Integer>();
        int predecessor = dest;
        backwards.add(dest);
        while(predecessor != src){
            predecessor = pathTable[predecessor][1];
            backwards.add(predecessor);
        }
        path = new int[backwards.size()];
        int fwd = 0;
        for(int i=backwards.size()-1; i>=0; i--){
            path[fwd] = backwards.get(i);
            fwd++;
        }
    }

    /**
     * Only to be called after path determined. Only looking at one slot. Need to generalize. Also doesn't work with condition that no slots available.
     * @return
     */
    public boolean determineSlots(int bandwidth){
        boolean pathFound = false;
        //int testSlot = 0;
        int startingSlot = 0;
        int[] range = new int[bandwidth]; //for generalization to more than one slot
        for(int i=startingSlot; i<bandwidth; i++){
            range[i] = i;
        }
        while(!pathFound && (startingSlot+bandwidth)<=pn.getNumSlots()) {
            if(pathWithSelectedSlotsAvailable(range)){
                pathFound = true;
            }
            else {
                startingSlot++;
                for(int i=0; i<bandwidth; i++){
                    range[i] = i+startingSlot;
                }
            }
        }

        /* Check for rejected connection */
        if(!pathFound){
            return false;
        }

        slots = new int[bandwidth];
        for(int i=0; i<bandwidth; i++){
            slots[i] = range[i];
        }
        return true; // success!

        //for generalization to more than one slot:
//        slots = new int[range.length]; //to make sure slots is the correct length
//        for(int i=0; i<range.length; i++){
//            slots[i] = range[i];
//        }
        //end stuff for generalization to more than one slot
    }

    /**
     * Check to see if the path is available in the current slot range.
     * @param slots
     * @return
     */
    public boolean pathWithSelectedSlotsAvailable(int[] slots){
        //int[] range = {slot};
        for(int i=0; i<path.length-1; i++){
            if(!pn.getNetwork()[path[i]][path[i+1]].isSlotRangeFree(slots) || !pn.getNetwork()[path[i+1]][path[i]].isSlotRangeFree(slots)){ //are the slots from node i to node i+1 in the path free?
                return false;
            }
        }
        return true;
    }

    // Getters and setters
    public PhysicalNetwork getPn() {
        return pn;
    }

    public void setPn(PhysicalNetwork pn) {
        this.pn = pn;
    }

    public int[] getSlots() {
        return slots;
    }

    public void setSlots(int[] slots) {
        this.slots = slots;
    }

    public boolean[] getIsVisited() {
        return isVisited;
    }

    public void setIsVisited(boolean[] isVisited) {
        this.isVisited = isVisited;
    }

    public int[][] getPathTable() {
        return pathTable;
    }

    public void setPathTable(int[][] pathTable) {
        this.pathTable = pathTable;
    }

    public int[] getPath() {
        return path;
    }

    public void setPath(int[] path) {
        this.path = path;
    }
}
