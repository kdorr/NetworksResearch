import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Kimberly Orr November 2018.
 * Class to read in and store the physical topology of the network.
 */
public class PhysicalNetwork {
    private String filepath;
    private int numNodes;
    private int numSlots;
    private int numConnections;
    private Edge[][] network;

    /**
     * Constructor.
     * @param filepath The filepath of the file with physical network data.
     */
    public PhysicalNetwork(String filepath) {
        this.filepath = filepath;
        numNodes = 0;
        numSlots = 0;
        numConnections = 0;
        network = new Edge[0][0];
    }

    /**
     * Reads file to create 2D array of Edges and read and store metadata.
     * @throws IOException
     */
    public void createNetwork() throws IOException {
        //get number of nodes, set numSlots
        Scanner s = null;
        ArrayList<String[]> ntwk = new ArrayList<String[]>();

        /**
         *  Read File into array list of String arrays (split by lines). Each ArrayList entry is a line split into
         *  entries in a String array of the line split by whitespace.
         */
        try {
            s = new Scanner(new BufferedReader(new FileReader(filepath)));
            while (s.hasNextLine()) {
                String line = s.nextLine().trim();
                ntwk.add(line.split("\\s+")); //"\s+" is regex for multicharacter whitespace
            }
        } finally {
            if (s != null) {
                s.close();
            }
        }

        /**
         * Parse 1st Line (metadata):
         * [0] = numNodes, [1] = numConnections, [2] = numSlots
         */
        numNodes = Integer.parseInt(ntwk.get(0)[0]); //TODO possible NumberFormatException
        numConnections = Integer.parseInt(ntwk.get(0)[1]);
        numSlots = Integer.parseInt(ntwk.get(0)[2]);

        /**
         * Parse Rest of Lines (edges in the network):
         * [0] = src, [1] = dest, [2] = numFibers, [3] = distance
         */
        network = new Edge[numNodes][numNodes];
        for(int i=1; i<ntwk.size(); i++){
            int src = Integer.parseInt(ntwk.get(i)[0]);
            int dest = Integer.parseInt(ntwk.get(i)[1]);
            int numFibers = Integer.parseInt(ntwk.get(i)[2]);
            int distance = Integer.parseInt(ntwk.get(i)[3]);

            network[src][dest] = new Edge(numSlots, numFibers, distance, src, dest);
            network[dest][src] = new Edge(numSlots, numFibers, distance, dest, src);  //bi-directional
        }
    }

    //toString

    public String toString(){
        String str = "filepath: " + filepath
                + " numSlots: " + numSlots
                + "\nnumNodes: " + numNodes
                + " numConnections: " + numConnections + "\n";
        for(int i=0; i<network.length; i++){
            str += "row " + i + "\n";
            for(int j=0; j<network[i].length; j++){
                str += "col " + j + " " + network[i][j] + " | ";
            }
            str += "\n";
        }
        str += "Output finished";
        return str;
    }

//    public String slotsToString(){
//        for(int i=0; i<network.length; i++){
//            for(int j=0; j<network.length; j++){
//
//            }
//        }
//    }

    //Getters and setters

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public int getNumSlots() {
        return numSlots;
    }

    public void setNumSlots(int numSlots) {
        this.numSlots = numSlots;
    }

    public int getNumConnections() {
        return numConnections;
    }

    public void setNumConnections(int numConnections) {
        this.numConnections = numConnections;
    }

    public Edge[][] getNetwork() {
        return network;
    }

    public void setNetwork(Edge[][] network) {
        this.network = network;
    }
}
