import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

/**
 * Created by Kimberly Orr on 8/29/18.
 * Class to generate traffic for the network including arrival times,
 * connection length, source and destination nodes, and bandwidth.
 */
public class TrafficGenerator {
    private double avgArrivalTime;
    private int minBandwidth;
    private int maxBandwidth;
    private double avgServiceTime;

    // Constructors

    public TrafficGenerator(){
        this.avgArrivalTime = 2; // arbitrary default
        this.minBandwidth = 1; // arbitrary default
        this.maxBandwidth = 16; // arbitrary default
        this.avgServiceTime = 5; // arbitrary default
    }

    //TODO: add constructor that handles parameters (read in from a file in NetworksResearch.java
    public TrafficGenerator(double avgArrivalTime, double avgServiceTime, int minBandwidth, int maxbandwidth){
        this.avgArrivalTime = avgArrivalTime;
        this.avgServiceTime = avgServiceTime;
        this.minBandwidth = minBandwidth;
        this.maxBandwidth = maxbandwidth;
    }

    /**
     * Create and setup a new Start Connection.
     * @param num This Connection's number (serves as an ID).
     * @param prevTime The time from the previous Connection.
     * @param nodeList The list of nodes to choose from for source and destination nodes.
     * @return Connection
     */
    public Connection newConnectionStart(int num, double prevTime, int[] nodeList){
        Connection start = new Connection();

        start.setIsEnd(false);

        start.setConnectionNum(num);

        ExponentialDistribution exp = new ExponentialDistribution(this.avgArrivalTime);
        start.calcTime(prevTime, exp.sample());

        start.pickSrcAndDest(nodeList);

        UniformIntegerDistribution bw = new UniformIntegerDistribution(this.minBandwidth, this.maxBandwidth);
        start.setBandwidth(bw.sample());

        return start;
    }

    /**
     * Create and setup a new End Connection.
     * @param start The Start Connection from this connection pair.
     * @return Connection.
     */
    public Connection newConnectionEnd(Connection start){
        Connection end = new Connection();

        end.setIsEnd(true);

        end.setConnectionNum(start.getConnectionNum());

        ExponentialDistribution exp = new ExponentialDistribution(this.avgServiceTime);
        end.calcTime(start.getTime(), exp.sample());

        end.setSrcNode(start.getSrcNode());

        end.setDestNode(start.getDestNode());

        end.setBandwidth(start.getBandwidth());

        return end;
    }
}
