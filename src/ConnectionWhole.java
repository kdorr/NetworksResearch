import java.util.Arrays;

public class ConnectionWhole {
    private int connectionID;
//    private double startTime;  //relevant?
//    private double endTime;  //relevant?
//    private int srcNode; //taken care of by virtual topology?
//    private int destNode;  //taken care of by virtual topology?
    private int[] slotsUsed;  //determined by routing algorithm
    private int[] path;  //determined by routing algorithm

    public ConnectionWhole(){

    }
    public ConnectionWhole(int connectionID, int[] slotsUsed, int[] path){
        this.connectionID = connectionID;
        this.slotsUsed = slotsUsed;
        this.path = path;
    }

    public String toString(){
        String str = "id: " + connectionID
                + "\nSlots: " + Arrays.toString(slotsUsed)
                + "\nPath: " + Arrays.toString(path);
        return str;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public int[] getSlotsUsed() {
        return slotsUsed;
    }

    public void setSlotsUsed(int[] slotsUsed) {
        this.slotsUsed = slotsUsed;
    }

    public int[] getPath() {
        return path;
    }

    public void setPath(int[] path) {
        this.path = path;
    }
}
