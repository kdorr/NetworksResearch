import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Kimberly Orr on 8/31/18. Heavily refactored 10/5/18-10/31/18.
 * Main program for network simulation.
 */
public class NetworksResearch {

    public static void main(String[] args) {
        //Read in Network
        PhysicalNetwork pNtwk = new PhysicalNetwork("ptDebug");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("main: IO Execption from reading the network caught");
        }
        // Establish virtual topology
        VirtualTopology vt = new VirtualTopology(pNtwk.getNumNodes());

        //Read in Parameters and create TrafficGenerator
        TrafficGenerator gen = new TrafficGenerator(2, 10000, 16); //arbitrary arrival and service times

        //Generate queue
        LinkedList<Connection> eventQueue = new LinkedList();

        //Initialize eventQueue with first connection
        double prevTime = 0;
        int idNum = 1;
        int numConnectionsToMake = 50; //TODO: make this a parameter to be read in
        int numRejectedConnections = 0;

        Connection start = gen.newConnectionStart(idNum, prevTime, pNtwk.getNumNodes());  //TODO: maybe initialize at beginning of main
        Connection end = gen.newConnectionEnd(start);  //TODO: maybe initialize at beginning of main
        start.setOther(end);
        eventQueue.add(start);
        eventQueue.add(end);
        numConnectionsToMake--;
        // Process events in the queue and create new nodes as needed.
        while(numConnectionsToMake > 0 || eventQueue.peek()!=null){
            Connection currentConnection = eventQueue.removeFirst();

            System.out.println(currentConnection.getConnectionNum() + ": src: " + currentConnection.getSrcNode() + ", dest: " + currentConnection.getDestNode() + ":");

            if(!currentConnection.getIsEnd()){  // Handle start nodes
                DijkstrasRoutingAlgorithm route = new DijkstrasRoutingAlgorithm(pNtwk);
                if(route.routeTraffic(currentConnection.getSrcNode(), currentConnection.getDestNode())){ //if a path was found
                    currentConnection.setSlotsUsed(route.getPath()); //in connection
                    currentConnection.setPath(route.getSlots());  //in connection
                    //End stuff done by routing algorithm
                    currentConnection.claimResources(pNtwk);  //physical network
                    vt.addConnection(currentConnection);
                    System.out.println("routed:\n" + vt.toString());
                }
                else{
                    numRejectedConnections++;
                    System.out.println("----------Rejected!!-------------");
                    //remove end node?
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
            }
            else{  // Handle end nodes
                //TODO: process end: release resources/update resources used
                System.out.println(currentConnection.getConnectionNum() + ": END");
                currentConnection.releaseResources(pNtwk);
                vt.removeConnection(currentConnection.getOther());
                System.out.println(vt.toString());
            }
        }
        System.out.println("rejected Connections: " + numRejectedConnections);
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

}
