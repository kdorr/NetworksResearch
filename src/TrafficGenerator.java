import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * Created by Kimberly Orr on 8/29/18
 * Class to generate traffic for the network including arrival times,
 * connection length, source and destination nodes, and bandwidth.
 */
public class TrafficGenerator {
    private double avgArrivalTime;
    private double[] arrivals;
    private double[] connectionLengths;

    public TrafficGenerator() {
        arrivals = new double[0];
    }

    public TrafficGenerator(int observations, double avgArrivalTime) {
        this.avgArrivalTime = avgArrivalTime;
        arrivals = generateArrivals(observations, avgArrivalTime);
        //connectionLengths = generateConnectionLengths(observations);
    }

    /**
     * Getters, setters, toString
     */
    public double getAvgArrivalTime() { return avgArrivalTime; }
    public double[] getArrivals() { return arrivals; }
    public double[] getConnectionLengths() { return connectionLengths; }
    public void setAvgArrivalTime(double avgArrivalTime) { this.avgArrivalTime = avgArrivalTime; }
    public void setArrivals(double[] arrivals) { this.arrivals = arrivals; }
    public void setConnectionLengths(double[] connectionLengths) { this.connectionLengths = connectionLengths; }
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

    private double[] generateConnectionLengths(int obs, double avgServiceTime) {
        ExponentialDistribution exp = new ExponentialDistribution(avgServiceTime);
        double[] connections = new double[obs];
        for(int i=0; i<obs; i++) {
            connections[i] = exp.sample();
        }
        return connections;
    }
}
