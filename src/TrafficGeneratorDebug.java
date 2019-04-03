import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

/**
 * Created by Kimberly Orr on 3/20/19.
 * Class to generate traffic for the network including arrival times,
 * connection length, source and destination nodes, and maxConnectionBandwidth for easy debugging.
 */
public class TrafficGeneratorDebug {
    private double avgArrivalTime;
    private double avgServiceTime;
    private int maxConnectionBandwidth;

    // Constructors

    public TrafficGeneratorDebug(){
        this.avgArrivalTime = 1; // arbitrary default
        this.avgServiceTime = 5; // arbitrary default
        this.maxConnectionBandwidth = 1; // arbitrary default
    }

    public TrafficGeneratorDebug(double avgArrivalTime, double avgServiceTime, int maxConnectionBandwidth){
        this.avgArrivalTime = avgArrivalTime;
        this.avgServiceTime = avgServiceTime;
        this.maxConnectionBandwidth = maxConnectionBandwidth;
    }

    /**
     * Create and setup a new Start Connection.
     * @param num This Connection's number (serves as an ID).
     * @param prevTime The time from the previous Connection.
     * @param src The src node (destination will be 2 nodes later)
     * @return Connection
     */
    public Connection newConnectionStart(int num, double prevTime, int src){
        int dest = src + 2;
        Connection start = new Connection();

        start.setIsEnd(false);

        start.setConnectionNum(num);

        //ExponentialDistribution exp = new ExponentialDistribution(this.avgArrivalTime);
        start.calcTime(prevTime, avgArrivalTime);

        //start.pickSrcAndDest(numNodes);
        start.pickSrcAndDestDebug(src, dest);

        UniformIntegerDistribution bw = new UniformIntegerDistribution(1, this.maxConnectionBandwidth);
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

        //ExponentialDistribution exp = new ExponentialDistribution(this.avgServiceTime);
        end.calcTime(start.getTime(), avgServiceTime);

        end.setSrcNode(start.getSrcNode());

        end.setDestNode(start.getDestNode());

        end.setBandwidth(start.getBandwidth());

        end.setOther(start);

        return end;
    }
}
