//import TrafficGenerator;

/**
 * Created by Kimberly Orr on 8/31/18.
 * Main program for the research project.
 */
public class NetworksResearch {

    public static void main(String[] args) {
        TrafficGenerator params = new TrafficGenerator(50, 3, 1);

        System.out.println("50 observations, avgArrivalTime = 3:\n" + params.toString());
    }
}
