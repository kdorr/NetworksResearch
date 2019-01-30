import java.util.ArrayList;
import java.util.Arrays;

public class DijkstrasRoutingAlgorithm {
    private PhysicalNetwork pn;
    private int[] slots;
    private boolean[] isVisited; //true if visited, false if unvisited.
    private int[][] pathTable; //1st dimension: node, 2nd dimension: 0-distance, 1-predecessor

    public DijkstrasRoutingAlgorithm(){
        isVisited = new boolean[0];
    }

    public DijkstrasRoutingAlgorithm(PhysicalNetwork pn){
        this.pn = pn;
        Edge[][] ntwk = pn.getNetwork(); //TODO: is this necessary??
        isVisited = new boolean[pn.getNodeList().length];
        for(int i=0; i<pn.getNodeList().length; i++){
            isVisited[i] = false;
        }
    }

    /**
     * -Start w/ source.
     * -For all of the nodes that aren't in the path, find the distance from source
     * to that node.
     * -Add the node w/ the minimum distance to path
     *
     * Note: a node is adjacent if pn.network[u][v] != null.
     * Infinity = Integer.MAX_VALUE;
     */
    public void calcTable(int src, int dest){
        int numNodes = pn.getNodeList().length;
        //TODO: use this.pathTable instead of table
        this.pathTable = new int[numNodes][2];
        //initialization
        for(int i=0; i<numNodes; i++){
            pathTable[i][0] = Integer.MAX_VALUE;
            pathTable[i][1] = -98; //Arbitrary value to indicate error
        }
        pathTable[src][0] = 0; //distance = 0
        pathTable[src][1] = src; //predecessor = src
        //isVisited[src] = true;

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
                if(!isVisited[i] && pn.getNetwork()[min][i]!=null){
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

    public int[] determinePath(int src, int dest){
        calcTable(src, dest);

        //once the table is calculated, get the path
        ArrayList<Integer> backwards = new ArrayList<Integer>();
        int predecessor = dest;
        backwards.add(dest);
        while(predecessor != src){
            predecessor = pathTable[predecessor][1];
            backwards.add(predecessor);
        }
        int[] path = new int[backwards.size()];
        int fwd = 0;
        for(int i=backwards.size()-1; i>=0; i--){
            path[fwd] = backwards.get(i);
            fwd++;
        }
        return path;
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
}
