import java.util.ArrayList;
import java.util.Arrays;

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
     * Helper function for now (will need to clean this up big time)
     * @param src
     * @param dest
     */
    public boolean routeTraffic(int src, int dest){
        determinePath(src, dest);
        if(determineSlots()){  //if the connection wasn't rejected.
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
            int min = Integer.MAX_VALUE;
            for(int i=0; i<numNodes; i++){
                if(pathTable[i][0] < min && !isVisited[i]){
                    min = i;
                }
            }
            //calculate distances for all adjacent nodes
            for(int i=0; i<numNodes; i++){
                if(!isVisited[i] && pn.getNetwork()[min][i]!=null){  //Dijkstra's the other way should modify this
                        if (pn.getNetwork()[min][i].getDistance() < pathTable[i][0]) {
                            pathTable[i][0] = pn.getNetwork()[min][i].getDistance();
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
    public boolean determineSlots(){
        boolean pathFound = false;
        int testSlot = 0;
        int[] range = {0}; //for generalization to more than one slot
        while(!pathFound && testSlot<pn.getNumSlots()) {
            if(pathWithSelectedSlotsAvailable(testSlot)){
                pathFound = true;
            }
            else {
                testSlot++;
            }
        }

        /* Check for rejected connection */
        if(!pathFound){
            return false;
        }

        slots = new int[1];
        slots[0] = testSlot;
        return true; // success!

        //for generalization to more than one slot:
//        slots = new int[range.length]; //to make sure slots is the correct length
//        for(int i=0; i<range.length; i++){
//            slots[i] = range[i];
//        }
        //end stuff for generalization to more than one slot
    }

    /**
     * maybe combine with determineSlots???
     * @param slot
     * @return
     */
    public boolean pathWithSelectedSlotsAvailable(int slot){
        int[] range = {slot};
        System.out.println("path: " + Arrays.toString(path));
        for(int i=0; i<path.length-1; i++){
            if(!pn.getNetwork()[path[i]][path[i+1]].isSlotRangeFree(range) || !pn.getNetwork()[path[i+1]][path[i]].isSlotRangeFree(range)){ //are the slots from node i to node i+1 in the path free?
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
