import java.util.LinkedList;
import java.util.ListIterator;

public class VirtualTopology {
    private LinkedList<Connection>[][] vt;

    public VirtualTopology(){
        vt = new LinkedList[0][0];
    }

    public VirtualTopology(int numNodes){
        vt = new LinkedList[numNodes][numNodes];
    }

//    public String toString(){
//        String str = "TODO";
//        return str;
//    }

    public LinkedList<Connection>[][] getVt() {
        return vt;
    }

    public void setVt(LinkedList<Connection>[][] vt) {
        this.vt = vt;
    }

    public Connection findConnection(int src, int dest, int id){
        Connection found = new Connection();
        if(vt[src][dest].peek()==null){
            System.err.println("There's nothing to find in the virtual topology"); //error? or checkNull to help determine if empty?
        }
        else{
            ListIterator<Connection> iter = vt[src][dest].listIterator(0);
            while(iter.hasNext()){
                Connection current = iter.next();
                if(current.getConnectionNum() == id){
                    found = current;
                    break;
                }
            }
        }
        return found;
    }

}
