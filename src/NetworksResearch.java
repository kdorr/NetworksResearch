import java.io.BufferedWriter;
import java.io.IOException;
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
        //Read in Parameters
        int numConnectionsToMake = 100;
        int numConnectionsMade = numConnectionsToMake;
        String slotUsageFile = "out/slotUsage.csv";
        String summaryStatsFile = "out/summaryStats.csv";
        String queueEventsFile = "out/queueEvents.csv";
        String queueEventsString = "";
        int snapshotFrequency = 5;

        //Read in Network
        PhysicalNetwork pNtwk = new PhysicalNetwork("ptDebug");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("main: IO Execption from reading the network caught");
        }

        //Set up output files
        setUpSlotUsage(slotUsageFile, pNtwk);

        // Establish virtual topology
        VirtualTopology vt = new VirtualTopology(pNtwk.getNumNodes());

        //Create TrafficGenerator
        TrafficGenerator gen = new TrafficGenerator(2, 3, 1); //arbitrary arrival and service times

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

            queueEventsString += "\n" + currentConnection.getConnectionNum() + ": src: " + currentConnection.getSrcNode() + ", dest: " + currentConnection.getDestNode() + ":";
//            System.out.println(currentConnection.getConnectionNum() + ": src: " + currentConnection.getSrcNode() + ", dest: " + currentConnection.getDestNode() + ":");

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
                    queueEventsString += "\nrouted: " +
                            "path:" + Arrays.toString(currentConnection.getPath())
                            + " slot: " + Arrays.toString(currentConnection.getSlotsUsed())
                            + "\n" + vt.toString();
//                    System.out.println("routed: " +
//                                    "path:" + Arrays.toString(currentConnection.getPath())
//                                    + " slot: " + Arrays.toString(currentConnection.getSlotsUsed())
//                                    + "\n" + vt.toString());
                }
                else{ //no path found
                    numRejectedConnections++;
                    queueEventsString += "\n----------Rejected!!-------------";
//                    System.out.println("----------Rejected!!-------------");
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
                if(currentConnection.getConnectionNum() % snapshotFrequency == 0){
                    slotUsage(slotUsageFile, pNtwk, currentConnection.getConnectionNum());
                }
//                if(Math.round(currentConnection.getTime()) % snapshotFrequency == 0){
//                    slotUsage(slotUsageFile, pNtwk, currentConnection.getTime());
//                }
            }
            /**
             * Handle end nodes
             */
            else{
                queueEventsString += "\n" + currentConnection.getConnectionNum() + ": END";
//                System.out.println(currentConnection.getConnectionNum() + ": END");
                currentConnection.releaseResources(pNtwk);
                vt.removeConnection(currentConnection.getOther());
                //System.out.println(vt.toString());
            }
        }
//        System.out.println("rejected Connections: " + numRejectedConnections);
        writeToFile(summaryStatsFile, "Total rejected connections: " + numRejectedConnections + "\nRejection percentage: " + (double)numRejectedConnections/numConnectionsMade);
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

    public static void edgeStress(PhysicalNetwork pn){
        //for upper triangle of physical links,
    }

}
