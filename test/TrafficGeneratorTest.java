import org.apache.commons.math3.distribution.GammaDistribution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrafficGeneratorTest {

    @Test
    /**
     * Tests that interarrival times follow a Poisson Process (exponentially distributed).
     */
    void interarrivalTimes(){
        int numConnections = 100000;
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
         * Calculate mean and variance of arrival times.
         * Sample mean = sum of all of the observations divided by the number of observations
         * Sample variance = (sum of (x_i - mean)^2) / (n-1)
         */
        double sum = 0;
        for(int i=0, j=1; j<arrivals.length; i++, j++){
            sum += arrivals[j]-arrivals[i];
        }
        double mean = sum / arrivals.length;

        //calculate variance
        double variance = 0;
        for(int i=0, j=1; j<arrivals.length; i++, j++){
            variance += ((arrivals[j]-arrivals[i])-mean)*((arrivals[j]-arrivals[i])-mean); //sum (x_i - E[x])^2
        }
        variance = variance/(arrivals.length-1);

        //check that the actual average interarrival time is approximately equal to avgArrivalTime
        assertEquals(mean, 2, .05);
        assertEquals(variance, 2*2, .05);
        //check that the nth arrival is approximately Gamma(n, lambda) (IN FOR LOOP)
        //check that it follows a poisson process: s = E[N(s)]/lambda
    }

    @Test
    /**
     * Test to check that the service times have the same average as requested.
     */
    void serviceTimes(){
        TrafficGenerator gen = new TrafficGenerator(2, 3, 1, 16);
        int numConnections = 100000;
        int[] nodeList = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] serviceTimes = new double[numConnections];

        double sumServiceTimes = 0;
        double varianceServTimes = 0;
        double prevTime=0;
        for(int i=0; i<serviceTimes.length; i++){
            //Generate connections
            Connection start = gen.newConnectionStart(i, prevTime, nodeList);
            prevTime = start.getTime();
            Connection end = gen.newConnectionEnd(start);
            double serviceTime = end.getTime() - start.getTime();
            serviceTimes[i] = serviceTime;
            sumServiceTimes += serviceTime;
        }
        double avgServiceTime = sumServiceTimes / numConnections;

        for(int i=0; i<serviceTimes.length; i++){
            varianceServTimes += (serviceTimes[i] - avgServiceTime)*(serviceTimes[i] - avgServiceTime);
        }
        varianceServTimes = varianceServTimes / (numConnections - 1);

        assertEquals(avgServiceTime, 3, .05);
        assertEquals(varianceServTimes, 3*3, .05);
    }

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