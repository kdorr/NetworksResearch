import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ParameterParsing {
    private String inputFile;
    boolean usingDetailedStats;
    int numConnectionsToMake;
    int avgArrivalTime;
    int avgServiceTime;
    int maxConnectionBandwidth;
    int snapshotFrequency;
    String networkFile;
    String slotUsageFile;
    String summaryStatsFile;
    String queueEventsFile;
    String edgeStressFile;

    public ParameterParsing(String file){
        this.inputFile = file;
        //defaults
        usingDetailedStats = false;
        numConnectionsToMake = 100;
        avgArrivalTime = 2;
        avgServiceTime = 4;
        maxConnectionBandwidth = 32;
        snapshotFrequency = 5;
        networkFile = "ptDebug";
        slotUsageFile = "out/slotUsage.csv";
        summaryStatsFile = "out/summaryStats.csv";
        queueEventsFile = "out/queueEvents.csv";
        edgeStressFile = "out/edgeStress.csv";
    }

    /**
     * Reads file to create 2D array of Edges and read and store metadata.
     * @throws IOException
     */
    public void readParams() throws IOException {
        //get number of nodes, set numSlots
        Scanner s = null;
        ArrayList<String[]> params = new ArrayList<String[]>();

        /**
         *  Read File into array list of String arrays (split by lines). Each ArrayList entry is a line split into
         *  entries in a String array of the line split by whitespace.
         */
        try {
            s = new Scanner(new BufferedReader(new FileReader(inputFile)));
            while (s.hasNextLine()) {
                String line = s.nextLine().trim();
                params.add(line.split("\\s+")); //"\s+" is regex for multicharacter whitespace
            }
        } finally {
            if (s != null) {
                s.close();
            }
        }

        switch (params.size()){
            case 9: usingDetailedStats = (Integer.parseInt(params.get(8)[0])==1) ? true : false;
            case 8: edgeStressFile = params.get(7)[0];
            case 7: queueEventsFile = params.get(6)[0];
            case 6: summaryStatsFile = params.get(5)[0];
            case 5: slotUsageFile = params.get(4)[0];
            case 4: networkFile = params.get(3)[0];
            case 3: snapshotFrequency = Integer.parseInt(params.get(2)[0]);
            case 2: avgArrivalTime = Integer.parseInt(params.get(1)[0]);
                    avgServiceTime = Integer.parseInt(params.get(1)[1]);
                    maxConnectionBandwidth = Integer.parseInt(params.get(1)[2]);
            case 1: numConnectionsToMake = Integer.parseInt(params.get(0)[0]);
                    break;
            default: System.err.println("ParameterParsing: Invalid parameter file");
        }
    }
}
