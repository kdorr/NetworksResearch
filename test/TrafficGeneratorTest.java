import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrafficGeneratorTest {

    @Test
    void getArrivals() {
    }

    @Test
    void generateArrivals() {
        double avgArrivalTime = 4;
        TrafficGenerator obs100000 = new TrafficGenerator(100000, avgArrivalTime);
        double[] arrivals = obs100000.getArrivals();
        double sum = 0;
        int j = 1;
        for(int i=0; i<arrivals.length-1; i++){
            sum += (arrivals[j]-arrivals[i]);
            j++;
        }
        double mean = sum / arrivals.length;
        assertTrue(mean > avgArrivalTime - 0.25 && mean < avgArrivalTime + 0.25);
    }

    @Test
    void generateConnections() {
        double avgServiceTime = 3;
        TrafficGenerator obs100000 = new TrafficGenerator(100000, 5, avgServiceTime);
        double[] connections = obs100000.getConnectionLengths();
        double sum = 0;
        for(double i : connections)
            sum += i;
        double mean = sum / connections.length;
        assertTrue(mean > avgServiceTime - 0.25 && mean < avgServiceTime + 0.25);
    }
}