import java.util.*;

/**
 * Created by Kimberly Orr on 8/31/18. Heavily refactored 10/5/18.
 * Main program for the research project.
 */
public class NetworksResearch {

    public static void main(String[] args) {
        //Read in Network
        int[] nodeList = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; //Temporary test list

        //Read in Parameters
        LinkedList<Connection> eventQueue = new LinkedList();
        TrafficGenerator gen = new TrafficGenerator(/* params */);
        //Add to eventQueue
        double prevTime = 0;
        int num = 1;
        Connection start = gen.newConnectionStart(num, prevTime, nodeList);
        Connection end = gen.newConnectionEnd(start);
        eventQueue.add(start);
        eventQueue.add(end);
        ListIterator<Connection> iter = eventQueue.listIterator(0);
        int count = 7;
        while(count > 0 && iter.hasNext()){
            Connection currentConnection = iter.next();
            if(!currentConnection.getIsEnd()){
                //process s1
                prevTime = start.getTime();
                num++;
                start = gen.newConnectionStart(num, prevTime, nodeList);
                end = gen.newConnectionEnd(start);
                insertInOrder(eventQueue, start);
                insertInOrder(eventQueue, end);
            }
            else{
                //process end
                System.out.println("end");
            }
            count--; //For temporary looping situation
        }
//        while(count > 0){
//            //process event:
//            // if start: route connection, update resources used, remove node from list
//            // if end: tear down and update resources used
//            // tear down = remove node from list, release resources
//            //create next Connection
//
//            // find last start node
//            //prevTime = eventQueue.get(eventQueue.lastIndexOf(start)).getTime();
//            prevTime = start.getTime();
//            num++;
//            start = gen.newConnectionStart(num, prevTime, nodeList);
//                    //prevTime needs to be the time of the last startNode in the list
//            end = gen.newConnectionEnd(start);
//            insertInOrder(eventQueue, start);
//            insertInOrder(eventQueue, end);
//
//            count--; //for temporary looping situation
//        }

        for(int i=0; i<eventQueue.size(); i++){
            System.out.println(eventQueue.get(i).toString() + "\n");
        }




    }

    public static void insertInOrder(LinkedList<Connection> eq, Connection cn){
        if(eq.peekLast().getTime() <= cn.getTime()){
            eq.add(cn);
        }
        else{
            ListIterator<Connection> liter = eq.listIterator(0);
            while(liter.hasNext()) {
                Connection currentCN = liter.next();
                if(currentCN.getTime() > cn.getTime()){
                    liter.previous();
                    liter.add(cn);
                    break;
                }
            }
        }
    }
}
