import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class NetworkReader {
    private String path;
    private int numNodes;
    private int bandwidth;

    public NetworkReader(String filepath) {
        path = filepath;
        numNodes = 5;
    }

    public Edge[][] createNetwork() throws IOException {
        //get number of nodes, set bandwidth
        Scanner s = null;
        ArrayList<String[]> ntwk = new ArrayList<String[]>();

        /**
         *  Read File
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
         * 1st Line:
         * [0] = numNodes, [1] = numConnections, [2] = bandwidth
         */
        numNodes = Integer.parseInt(ntwk.get(0)[0]); //TODO possible NumberFormatException
        int numConnections = Integer.parseInt(ntwk.get(0)[1]);
        int bandwidth = Integer.parseInt(ntwk.get(0)[2]);

        /**
         * Rest of lines:
         * [0] = src, [1] = dest, [2] = numFibers, [3] = distance
         */
        Edge[][] network = new Edge[numNodes][numNodes];
        for(int i=1; i<ntwk.size(); i++){
            int src = Integer.parseInt(ntwk.get(i)[0]);
            int dest = Integer.parseInt(ntwk.get(i)[1]);
            int numFibers = Integer.parseInt(ntwk.get(i)[2]);
            int distance = Integer.parseInt(ntwk.get(i)[3]);
            network[src][dest] = new Edge(bandwidth, distance, src, dest);
            network[dest][src] = new Edge(bandwidth, distance, dest, src);
        }

        return network;
    }
}
