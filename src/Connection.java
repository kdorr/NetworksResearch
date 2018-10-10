import org.apache.commons.math3.distribution.UniformIntegerDistribution;

public class Connection {
    private boolean isEnd;
    private int connectionNum;
    private double time;
    private int srcNode;
    private int destNode;
    private int bandwidth;

    public Connection(){
    }

    public void calcTime(double prevTime, double interarrival){
        this.setTime(prevTime + interarrival);
    }

    public void pickSrcAndDest(int[] nodeList){
        UniformIntegerDistribution uni = new UniformIntegerDistribution(0, nodeList.length-1);
        this.setSrcNode(nodeList[uni.sample()]);
        do {
            this.setDestNode(nodeList[uni.sample()]);
        } while(this.getDestNode() == this.getSrcNode());
    }

    public boolean getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(boolean end) {
        isEnd = end;
    }

    public int getConnectionNum() {
        return connectionNum;
    }

    public void setConnectionNum(int connectionNum) {
        this.connectionNum = connectionNum;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(int srcNode) {
        this.srcNode = srcNode;
    }

    public int getDestNode() {
        return destNode;
    }

    public void setDestNode(int destNode) {
        this.destNode = destNode;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String toString(){
        String startOrEnd = "";
        if(isEnd)
            startOrEnd = "End";
        else
            startOrEnd = "Start";
        return /*"isEnd: " + this.isEnd*/ startOrEnd + " " + this.connectionNum
                + "\nTime: " + this.time
                + "\nsrcNode, destNode: (" + this.srcNode + ", " + this.destNode + ")";
                //")\nbandwidth: " + this.bandwidth;
    }

}
