import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Kimberly Orr on 8/31/18. Heavily refactored 10/5/18-10/31/18.
 * Main program for network simulation.
 */
public class NetworksResearch {

    public static void main(String[] args) {
        //Read in Network
        int[] nodeList = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; //Temporary test list

        //Read in Parameters and create TrafficGenerator
        TrafficGenerator gen = new TrafficGenerator(/* params */);

        //Generate queue
        LinkedList<Connection> eventQueue = new LinkedList();

        //Initialize eventQueue with first connection
        double prevTime = 0;
        int idNum = 1;
        int numConnectionsToMake = 100;
        Connection start = gen.newConnectionStart(idNum, prevTime, nodeList);  //TODO: maybe initialize at beginning of main
        Connection end = gen.newConnectionEnd(start);  //TODO: maybe initialize at beginning of main
        eventQueue.add(start);
        eventQueue.add(end);
        numConnectionsToMake--;
        // Process events in the queue and create new nodes as needed.
        while(numConnectionsToMake > 0 || eventQueue.peek()!=null){
            Connection currentConnection = eventQueue.removeFirst();

            System.out.println(currentConnection.toString() + "\n");
            if(!currentConnection.getIsEnd()){  // Handle start nodes
                //TODO: process start: route connection, update resources used

                //Create new connections
                if(numConnectionsToMake > 0) {
                    prevTime = start.getTime();
                    idNum++;
                    start = gen.newConnectionStart(idNum, prevTime, nodeList);
                    end = gen.newConnectionEnd(start);
                    insertInOrder(eventQueue, start);
                    insertInOrder(eventQueue, end);
                    numConnectionsToMake--;
                }
            }
            else{  // Handle end nodes
                //TODO: process end: release resources/update resources used
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
