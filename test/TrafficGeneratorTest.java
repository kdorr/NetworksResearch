import org.apache.commons.math3.distribution.GammaDistribution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrafficGeneratorTest {

    @Test
    /**
     * Tests that interarrival times follow a Poisson Process.
     */
    void interarrivalTimes(){
        int numConnections = 10000;
        Connection[] startConnects = new Connection[numConnections];
        double[] arrivals = new double[numConnections];

        /**
         *  Create Connections and add their times to arrivals.
         */
        TrafficGenerator gen = new TrafficGenerator(2, 3, 1, 16);
        int[] nodeList = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double prevTime=0;
        for(int i=0; i<startConnects.length; i++){
            startConnects[i] = gen.newConnectionStart(i, prevTime, nodeList);
            prevTime = startConnects[i].getTime();

            arrivals[i] = prevTime;  // add arrival time to list of arrivals
        }

        /**
         * Calculate mean of arrival times.
         */
        double sum = 0;
        double variance = 0;
        for(int i=0, j=1; j<arrivals.length; i++, j++){
            sum += arrivals[j]-arrivals[i];
            variance += ((arrivals[j]-arrivals[i])-(sum/j))*((arrivals[j]-arrivals[i])-(sum/j)); // need to check (esp for off by one)
//            if(i>500) {
//                assertEquals(sum, i * 2, 1);
//            }
        }
        double mean = sum / arrivals.length;
        variance = variance/arrivals.length; //should be avg arrival time ** 2
        System.out.println(variance);

        //check that the actual average interarrival time is approximately equal to avgArrivalTime
        assertEquals(mean, 2, .05);
        //check that the nth arrival is approximately Gamma(n, lambda) (IN FOR LOOP)
        //check that it follows a poisson process: s = E[N(s)]/lambda
    }
//    @Test
//    void getArrivals() {
//    }
//
//    @Test
//    void generateArrivals() {
//        double avgArrivalTime = 4;
//        TrafficGenerator obs100000 = new TrafficGenerator(100000, avgArrivalTime);
//        double[] arrivals = obs100000.getArrivals();
//        double sum = 0;
//        int j = 1;
//        for(int i=0; i<arrivals.length-1; i++){
//            sum += (arrivals[j]-arrivals[i]);
//            j++;
//        }
//        double mean = sum / arrivals.length;
//        assertTrue(mean > avgArrivalTime - 0.25 && mean < avgArrivalTime + 0.25);
//    }
//
//    @Test
//    void generateConnections() {
//        double avgServiceTime = 3;
//        TrafficGenerator obs100000 = new TrafficGenerator(100000, 5, avgServiceTime);
//        double[] connections = obs100000.getConnectionLengths();
//        double sum = 0;
//        for(double i : connections)
//            sum += i;
//        double mean = sum / connections.length;
//        assertTrue(mean > avgServiceTime - 0.25 && mean < avgServiceTime + 0.25);
//    }
//
//    @Test
//    void generateSrcAndDest() {
//        TrafficGenerator obs10 = new TrafficGenerator(10, 5, 1, 5);
//        double[][] sd = obs10.getSourcesAndDestinations();
//        for(int i=0; i<sd.length; i++){
//            assertTrue(sd[i][0] != sd[i][1]);
//        }
//    }
}