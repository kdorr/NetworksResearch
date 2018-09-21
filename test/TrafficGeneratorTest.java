import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrafficGeneratorTest {

    @Test
    void getArrivals() {
    }

    @Test
    void generateArrivals() {
        TrafficGenerator obs100000 = new TrafficGenerator(100000, 4);
        double[] arrivals = obs100000.getArrivals();
        double sum = 0;
        int j = 1;
        for(int i=0; i<arrivals.length-1; i++){
            sum += (arrivals[j]-arrivals[i]);
            j++;
        }
        double mean = sum / arrivals.length;
        assertTrue(mean > obs100000.getAvgArrivalTime() - 0.25 && mean < obs100000.getAvgArrivalTime() + 0.25);
    }
}