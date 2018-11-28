import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

/**
 * Created by Kimberly Orr on 8/29/18.
 * Class to generate traffic for the network including arrival times,
 * connection length, source and destination nodes, and bandwidth.
 */
public class TrafficGenerator {
    private double avgArrivalTime;
    private double avgServiceTime;
    private int bandwidth;

    // Constructors

    public TrafficGenerator(){
        this.avgArrivalTime = 2; // arbitrary default
        this.avgServiceTime = 5; // arbitrary default
        this.bandwidth = 16; // arbitrary default
    }

    //TODO: add constructor that handles parameters (read in from a file in NetworksResearch.java
    public TrafficGenerator(double avgArrivalTime, double avgServiceTime, int bandwidth){
        this.avgArrivalTime = avgArrivalTime;
        this.avgServiceTime = avgServiceTime;
        this.bandwidth = bandwidth;
    }

    /**
     * Create and setup a new Start Connection.
     * @param num This Connection's number (serves as an ID).
     * @param prevTime The time from the previous Connection.
     * @param numNodes The number of nodes in the physical network.
     * @return Connection
     */
    public Connection newConnectionStart(int num, double prevTime, int numNodes){
        Connection start = new Connection();

        start.setIsEnd(false);

        start.setConnectionNum(num);

        ExponentialDistribution exp = new ExponentialDistribution(this.avgArrivalTime);
        start.calcTime(prevTime, exp.sample());

        start.pickSrcAndDest(numNodes);

        UniformIntegerDistribution bw = new UniformIntegerDistribution(1, this.bandwidth);
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
