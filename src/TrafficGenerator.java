import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

/**
 * Created by Kimberly Orr on 8/29/18.
 * Class to generate traffic for the network including arrival times,
 * connection length, source and destination nodes, and bandwidth.
 */
public class TrafficGenerator {
    private double[] arrivals;
    private double[] connectionLengths;
    private double[][] sourcesAndDestinations; //2 arrays or one?

    public TrafficGenerator() {
        arrivals = new double[0];
    }

    public TrafficGenerator(int observations) {
        this.arrivals = generateArrivals(observations, 5);
        this.connectionLengths = generateConnectionLengths(observations, 1);
        this.sourcesAndDestinations = generateSourceAndDestinationNode(observations, 50);
    }

    public TrafficGenerator(int observations, double avgArrivalTime) {
        this.arrivals = generateArrivals(observations, avgArrivalTime);
        this.connectionLengths = generateConnectionLengths(observations, 1);
        this.sourcesAndDestinations = generateSourceAndDestinationNode(observations, 50);
    }

    public TrafficGenerator(int observations, double avgArrivalTime, double avgServiceTime) {
        this.arrivals = generateArrivals(observations, avgArrivalTime);
        this.connectionLengths = generateConnectionLengths(observations, avgServiceTime);
        this.sourcesAndDestinations = generateSourceAndDestinationNode(observations, 50);
    }

    public TrafficGenerator(int observations, double avgArrivalTime, double avgServiceTime, int numNodes){
        this.arrivals = generateArrivals(observations, avgArrivalTime);
        this.connectionLengths = generateConnectionLengths(observations, avgServiceTime);
        this.sourcesAndDestinations = generateSourceAndDestinationNode(observations, numNodes);
    }

    /**
     * Getters, setters, toString
     */
    public double[] getArrivals() { return arrivals; }
    public double[] getConnectionLengths() { return connectionLengths; }
    public double[][] getSourcesAndDestinations() {
        return sourcesAndDestinations;
    }
    public void setArrivals(double[] arrivals) { this.arrivals = arrivals; }
    public void setConnectionLengths(double[] connectionLengths) { this.connectionLengths = connectionLengths; }
    public void setSourcesAndDestinations(double[][] sourcesAndDestinations) {
        this.sourcesAndDestinations = sourcesAndDestinations;
    }

    public String toString() {
        String arrs = "";
        for(int i=0; i<arrivals.length; i++) {
            arrs += arrivals[i] + ", ";
        }
        return arrs;
    }

    /**
     * Generate the arrival times.
     * @param obs
     * @param avgArrivalTime
     * @return arrs
     */
    private double[] generateArrivals(int obs, double avgArrivalTime) {
        ExponentialDistribution exp = new ExponentialDistribution(avgArrivalTime);
        double[] arrivals = new double[obs];

        double previousArrival = 0;
        for(int i=0; i<obs; i++) {
            arrivals[i] = previousArrival + exp.sample();
            previousArrival = arrivals[i];
        }

        return arrivals;
    }

    /**
     * Generate the connection lengths.
     * @param obs
     * @param avgServiceTime
     * @return
     */
    private double[] generateConnectionLengths(int obs, double avgServiceTime) {
        ExponentialDistribution exp = new ExponentialDistribution(avgServiceTime);
        double[] connections = new double[obs];
        for(int i=0; i<obs; i++) {
            connections[i] = exp.sample();
        }
        return connections;
    }

    /**
     * Generate source and destination nodes for each observation.
     * Source and Destination cannot be the same node. Do I want to separate into 2 different
     * functions/2 arrays? If this were python, I'd return a list of tuple pairs (s, d).
     * Does it make more sense to return a list of sources and a list of destinations or return
     * a list with paired source and destination nodes for each observation?
     * @param obs
     * @param numNodes
     * @return
     */
    private double[][] generateSourceAndDestinationNode(int obs, int numNodes) {
        UniformIntegerDistribution uni = new UniformIntegerDistribution(0, numNodes-1);
        double[][] srcAndDest = new double[obs][];
        for(int i=0; i<srcAndDest.length; i++) {
            srcAndDest[i] = new double[2];
            do {
                srcAndDest[i][0] = uni.sample(); //source
                srcAndDest[i][1] = uni.sample(); //destination
            } while(srcAndDest[i][0]==srcAndDest[i][1]);
            //srcAndDest[i] = generateUniqueSrcAndDest(numNodes);
        }
        return srcAndDest;
    }

    private double[] generateUniqueSrcAndDest(int numNodes){
        UniformIntegerDistribution uni = new UniformIntegerDistribution(0, numNodes-1);
        double[] sd = new double[2];
        do {
            sd[0] = uni.sample(); // source
            sd[1] = uni.sample(); // destination
        } while(sd[0] == sd[1]);
        return sd;
    }
}
