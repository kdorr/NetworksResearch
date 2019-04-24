import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Kimberly Orr on 8/31/18. Heavily refactored 10/5/18-10/31/18.
 * Main program for network simulation.
 */
public class NetworksResearch {

    public static void main(String[] args) {

        String inputFile = "";
        if(args.length != 1){
            System.err.println("Expecting one (string) argument with the file name");
        }
        else {
            inputFile = args[0];
        }
        //Read in Parameters
        ParameterParsing parser = new ParameterParsing(inputFile);
        try {
            parser.readParams();
        } catch (IOException e){
            System.err.println("main: IO Execption from reading the parameter file");
        }

        /**
         * TODO: Clean this up a lot
         */
        boolean usingDetailedStats = parser.usingDetailedStats;
        int numConnectionsToMake = parser.numConnectionsToMake;
        int numConnectionsMade = numConnectionsToMake; //helpful for summary stats
        int avgArrivalTime = parser.avgArrivalTime;
        int avgServiceTime = parser.avgServiceTime;
        int maxConnectionBandwidth = parser.maxConnectionBandwidth;
        int snapshotFrequency = parser.snapshotFrequency;
        String networkFile = parser.networkFile;
        //output file names and setup
        String slotUsageFile = parser.slotUsageFile;
        String summaryStatsFile = parser.summaryStatsFile;
        String queueEventsFile = parser.queueEventsFile;
        String edgeStressFile = parser.edgeStressFile;
        createFile(slotUsageFile);
        createFile(summaryStatsFile);
        createFile(queueEventsFile);
        createFile(edgeStressFile);
        // initialization
        String queueEventsString = "";


        //Read in Network
        PhysicalNetwork pNtwk = new PhysicalNetwork(networkFile);
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("main: IO Execption from reading the network caught");
        }

        //Set up output files
        if(usingDetailedStats) {
            setUpSlotUsage(slotUsageFile, pNtwk);
            setUpEdgeStress(edgeStressFile, pNtwk);
        }
        // Establish virtual topology
        VirtualTopology vt = new VirtualTopology(pNtwk.getNumNodes());

        //Create TrafficGenerator
        TrafficGenerator gen = new TrafficGenerator(avgArrivalTime, avgServiceTime, maxConnectionBandwidth); //arbitrary arrival and service times

        //Generate queue
        LinkedList<Connection> eventQueue = new LinkedList();

        //Initialize eventQueue with first connection
        double prevTime = 0;
        int idNum = 1;
        int numRejectedConnections = 0;

        Connection start = gen.newConnectionStart(idNum, prevTime, pNtwk.getNumNodes());
        Connection end = gen.newConnectionEnd(start);
        start.setOther(end);
        eventQueue.add(start);
        eventQueue.add(end);
        numConnectionsToMake--;

        // Process events in the queue and create new nodes as needed.
        while(numConnectionsToMake > 0 || eventQueue.peek()!=null){
            Connection currentConnection = eventQueue.removeFirst();

            if(usingDetailedStats)
                queueEventsString += "\n" + currentConnection.getConnectionNum() + ": src: " + currentConnection.getSrcNode() + ", dest: " + currentConnection.getDestNode() + ":";

            /**
             * Handle start nodes
             */
            if(!currentConnection.getIsEnd()){
                DijkstrasRoutingAlgorithm route = new DijkstrasRoutingAlgorithm(pNtwk);
                if(route.routeTraffic(currentConnection.getSrcNode(), currentConnection.getDestNode())){ //if a path was found
                    currentConnection.setPath(route.getPath());
                    currentConnection.setSlotsUsed(route.getSlots());
                    currentConnection.claimResources(pNtwk);
                    vt.addConnection(currentConnection);
                    if(usingDetailedStats) {
                        queueEventsString += "\nrouted: " +
                                "path:" + Arrays.toString(currentConnection.getPath())
                                + " slot: " + Arrays.toString(currentConnection.getSlotsUsed())
                                + "\n" + vt.toString();
                    }
                }
                else{ //no path found
                    numRejectedConnections++;
                    if(usingDetailedStats)
                        queueEventsString += "\n----------Rejected!!-------------";

                    //remove the end connection from the queue
                    eventQueue.remove(currentConnection.getOther());
                }

                //Create new connections
                if(numConnectionsToMake > 0) {
                    prevTime = start.getTime();
                    idNum++;
                    start = gen.newConnectionStart(idNum, prevTime, pNtwk.getNumNodes());
                    end = gen.newConnectionEnd(start);
                    start.setOther(end);
                    insertInOrder(eventQueue, start);
                    insertInOrder(eventQueue, end);
                    numConnectionsToMake--;
                }

                /**
                 * Network snapshots
                 */
                if(usingDetailedStats && currentConnection.getConnectionNum() % snapshotFrequency == 0){
                    slotUsage(slotUsageFile, pNtwk, currentConnection.getConnectionNum());
                    edgeStress(edgeStressFile, pNtwk, currentConnection.getConnectionNum());
                }
//                if(Math.round(currentConnection.getTime()) % snapshotFrequency == 0){
//                    slotUsage(slotUsageFile, pNtwk, currentConnection.getTime());
//                }
            }
            /**
             * Handle end nodes
             */
            else{
                if(usingDetailedStats)
                    queueEventsString += "\n" + currentConnection.getConnectionNum() + ": END";

                currentConnection.releaseResources(pNtwk);
                vt.removeConnection(currentConnection.getOther());

            }
        }

        writeToFile(summaryStatsFile, "numConnections: " + numConnectionsMade + ", avgArrivalTime: " + avgArrivalTime
                + ", avgServiceTime: " + avgServiceTime
                + "\nTotal rejected connections:\n" + numRejectedConnections
                + "\nRejection percentage:\n" + (double)numRejectedConnections/numConnectionsMade);
        if(usingDetailedStats)
            writeToFile(queueEventsFile, queueEventsString);
    }

    /**
     * Inserts a connection into the event queue in order according to the connection's time.
     * @param eq the event queue
     * @param cn the connection to be added
     */
    public static void insertInOrder(LinkedList<Connection> eq, Connection cn){
        if(eq.peek()==null){  // eq is empty. TODO: test this.
            eq.add(cn);
        }
        else if(eq.peekLast().getTime() <= cn.getTime()){  // The new connection occurs after any existing connection
            eq.add(cn);
        }
        else{   // Find the spot where the connection should be inserted and add it to the queue
            ListIterator<Connection> iter = eq.listIterator(0);
            while(iter.hasNext()) {
                Connection currentCN = iter.next();
                if(currentCN.getTime() > cn.getTime()){
                    iter.previous();
                    iter.add(cn);
                    break;
                }
            }
        }
    }

    public static void createFile(String file){
        Path path = Paths.get(file);
        try {
            Writer fw = new FileWriter(file, false);
            fw.close();
        }
        catch(IOException e){
            System.err.format("IOExecption: %s%n", e);
        }
    }
    public static void writeToFile(String file, String s){
        Path path = Paths.get(file);
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            writer.write(s + "\n");
        } catch( IOException x){
            System.err.format("IOException: %s%n", x);
        }
    }

    public static void setUpSlotUsage(String file, PhysicalNetwork pn){
        String header = "connectionID,";
        for(int i=0; i<pn.getNumSlots()-1; i++){
            header += i + ",";
        }
        header += pn.getNumSlots()-1;
        writeToFile(file, header);
    }
    public static void slotUsage(String file, PhysicalNetwork pn, double time){
        int[] counts = new int[pn.getNumSlots()];
        //for upper triangle of physical links, count usage
        for(int r=0; r<pn.getNumNodes(); r++){
            for(int c=r+1; c<pn.getNumNodes(); c++){
                for(int slot=0; slot<pn.getNumSlots(); slot++){
                    if(pn.getNetwork()[r][c]!=null && pn.getNetwork()[r][c].getSlots()[slot]) {
                        counts[slot]++;
                    }
                }
            }
        }

        String output = time + ",";
        for(int i=0; i<counts.length-1; i++){ //is there a way to do this w/out counts array?
            output += counts[i] + ",";
        }
        output += counts[counts.length-1]; //so the last one doesn't include a comma

        writeToFile(file, output);
    }

    public static void setUpEdgeStress(String file, PhysicalNetwork pn){
        String output = "ID,";
        for(int r=0; r<pn.getNumNodes(); r++){
            for(int c=r+1; c<pn.getNumNodes(); c++) {
                output += (r>=pn.getNumNodes()-2 && c>=pn.getNumNodes()-1) ? (r + "" + c) : (r + "" + c + ",");
            }
        }
        writeToFile(file, output);
    }
    public static void edgeStress(String file, PhysicalNetwork pn, double time){
        int numInUse = 0;
        String output = time + ",";

        //for the upper triangle
        for(int r=0; r<pn.getNumNodes(); r++){
            for(int c=r+1; c<pn.getNumNodes(); c++){
                numInUse = 0;
                //for each slot
                for(int slot=0; slot<pn.getNumSlots(); slot++){
                    if(pn.getNetwork()[r][c]!=null && pn.getNetwork()[r][c].getSlots()[slot]) {
                        numInUse++;
                    }
                }

                // If not the last edge, calculate the percentage of slots in use for that edge and add a comma to the
                // end of the output. If the last edge, don't add a comma at the end.
                output += (r>=pn.getNumNodes()-2 && c>=pn.getNumNodes()-1)
                        ? (double)numInUse/(double)pn.getNumSlots()
                        : ((double)numInUse / (double)pn.getNumSlots()) + ",";
            }
        }
        writeToFile(file, output);
    }

}
