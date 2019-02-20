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
        PhysicalNetwork pNtwk = new PhysicalNetwork("pt6");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }
        // Establish virtual topology
        VirtualTopology vt = new VirtualTopology(pNtwk.getNumNodes());

        //Read in Parameters and create TrafficGenerator
        TrafficGenerator gen = new TrafficGenerator(2, 5, 16); //arbitrary arrival and service times

        //Generate queue
        LinkedList<Connection> eventQueue = new LinkedList();

        //Initialize eventQueue with first connection
        double prevTime = 0;
        int idNum = 1;
        int numConnectionsToMake = 10; //TODO: make this a parameter to be read in

        Connection start = gen.newConnectionStart(idNum, prevTime, pNtwk.getNumNodes());  //TODO: maybe initialize at beginning of main
        Connection end = gen.newConnectionEnd(start);  //TODO: maybe initialize at beginning of main
        eventQueue.add(start);
        eventQueue.add(end);
        numConnectionsToMake--;
        // Process events in the queue and create new nodes as needed.
        while(numConnectionsToMake > 0 || eventQueue.peek()!=null){
            Connection currentConnection = eventQueue.removeFirst();

            System.out.println(currentConnection.getConnectionNum() + ":\n" + vt.toString());

            if(!currentConnection.getIsEnd()){  // Handle start nodes
                //TODO: process start: route connection, update resources used
                //Start stuff done by routing algorithm

                int[] changeMeSlots = {2}; //dummy array that contains the number of slots used
                int[] changeMePath = {currentConnection.getSrcNode(), currentConnection.getDestNode()}; //dummy array for path (start to end)
                currentConnection.setSlotsUsed(changeMeSlots); //in connection
                currentConnection.setPath(changeMePath);  //in connection
                //End stuff done by routing algorithm
                currentConnection.claimResources(pNtwk);  //physical network
                vt.addConnection(currentConnection);

                //Create new connections
                if(numConnectionsToMake > 0) {
                    prevTime = start.getTime();
                    idNum++;
                    start = gen.newConnectionStart(idNum, prevTime, pNtwk.getNumNodes());
                    end = gen.newConnectionEnd(start);
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
            }
        }
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
