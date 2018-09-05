import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * Created by Kimberly Orr on 8/29/18
 * Class to come up with arrival times for events
 */
public class TrafficGenerator {
    private double[] arrivals;

    public TrafficGenerator() {
        arrivals = new double[0];
    }

    public TrafficGenerator(int observations, double avgArrivalTime) {
        arrivals = generateArrivals(observations, avgArrivalTime);
    }

    /**
     * Getters, setters, toString
     */
    public double[] getArrivals() {
        return arrivals;
    }
    public void setArrivals(double[] arrivals) { this.arrivals = arrivals; }
    public String toString() {
        String arrs = "";
        for(int i=0; i<arrivals.length; i++) {
            arrs += arrivals[i] + ", ";
        }
        return arrs;
    }

    private double[] generateArrivals(int obs, double avgArrivalTime) {
        //PoissonDistribution pd = new PoissonDistribution(avgArrivalTime);
        ExponentialDistribution exp = new ExponentialDistribution(avgArrivalTime);
        double[] arrs = new double[obs];

        double previousArrival = 0;
        for(int i=0; i<obs; i++) {
            //arrs[i] = pd.sample();
            System.out.print(exp.sample() + ", ");
            arrs[i] = previousArrival + exp.sample();
            previousArrival = arrs[i];
        }

        return arrs;
    }
}
