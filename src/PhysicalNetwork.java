import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Kimberly Orr November 2018.
 * Class to read in and store the physical topology of the network.
 */
public class PhysicalNetwork {
    private String path;
    private int numNodes;
    private int bandwidth;
    private int numConnections;
    private Edge[][] network;

    /**
     * Constructor.
     * @param filepath The path of the file with physical network data.
     */
    public PhysicalNetwork(String filepath) {
        path = filepath;
        numNodes = 0;
        bandwidth = 0;
        numConnections = 0;
        network = new Edge[0][0];
        try {
            createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }
    }

    /**
     * Reads file to create 2D array of Edges and read and store metadata.
     * @throws IOException
     */
    public void createNetwork() throws IOException {
        //get number of nodes, set bandwidth
        Scanner s = null;
        ArrayList<String[]> ntwk = new ArrayList<String[]>();

        /**
         *  Read File into array list of String arrays (split by lines). Each ArrayList entry is a line split into
         *  entries in a String array of the line split by whitespace.
         */
        try {
            s = new Scanner(new BufferedReader(new FileReader(path)));
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
         * [0] = numNodes, [1] = numConnections, [2] = bandwidth
         */
        numNodes = Integer.parseInt(ntwk.get(0)[0]); //TODO possible NumberFormatException
        numConnections = Integer.parseInt(ntwk.get(0)[1]);
        bandwidth = Integer.parseInt(ntwk.get(0)[2]);

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

            network[src][dest] = new Edge(bandwidth, distance, src, dest);
            network[dest][src] = new Edge(bandwidth, distance, dest, src);  //bi-directional
        }
    }

    //toString

    public String toString(){
        String str = "path: " + path
                + " bandwidth: " + bandwidth
                + "\nnumNodes: " + numNodes
                + " numConnections: " + numConnections + "\n";
        for(int i=0; i<network.length; i++){
            for(int j=0; j<network.length; j++){
                str += network[i][j].getDistance() + " | ";
            }
            str += "\n";
        }
        return str;
    }

    //Getters and setters

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
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
