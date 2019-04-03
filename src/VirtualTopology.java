import java.util.LinkedList;
import java.util.ListIterator;

public class VirtualTopology {
    private LinkedList<Connection>[][] vt;

    public VirtualTopology(){
        vt = new LinkedList[0][0];
    }

    public VirtualTopology(int numNodes){
        vt = new LinkedList[numNodes][numNodes];
        for(int i=0; i<vt.length; i++){
            for(int j=0; j<vt[i].length; j++){
                vt[i][j] = new LinkedList<Connection>();
            }
        }
    }

    public String toString(){
        String str = "";
        for(int i=0; i<vt.length; i++){
            for(int j=0; j<vt[i].length; j++){
                str += vt[i][j].size() + ", ";
            }
            str += "\n";
        }
        return str;
    }

    /**
     * Add connection to the virtual topology.
     * @param c
     */
    public void addConnection(Connection c){
        vt[c.getSrcNode()][c.getDestNode()].add(c);
    }

    /**
     * Remove connection from the virtual topology.
     * @param c
     */
    public void removeConnection(Connection c){
        vt[c.getSrcNode()][c.getDestNode()].remove(c);
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

    public LinkedList<Connection>[][] getVt() {
        return vt;
    }

    public void setVt(LinkedList<Connection>[][] vt) {
        this.vt = vt;
    }

}
