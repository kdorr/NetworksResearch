import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DijkstraTest {
    @Test
    void calcTableTest(){
        PhysicalNetwork pNtwk = new PhysicalNetwork("pt6");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }

        DijkstrasRoutingAlgorithm test = new DijkstrasRoutingAlgorithm(pNtwk);
        test.calcTable(0, 3);

        //Test distances (expected worked out on paper)
        assertEquals(0, test.getPathTable()[0][0]);
        assertEquals(500, test.getPathTable()[1][0]);
        assertEquals(1000, test.getPathTable()[2][0]);
        assertEquals(600, test.getPathTable()[3][0]);
        assertEquals(600, test.getPathTable()[4][0]);
        assertEquals(500, test.getPathTable()[5][0]);
        //Test predecessors (expected worked out on paper)
        assertEquals(0, test.getPathTable()[0][1]);
        assertEquals(0, test.getPathTable()[1][1]);
        assertEquals(1, test.getPathTable()[2][1]);
        assertEquals(2, test.getPathTable()[3][1]);
        assertEquals(3, test.getPathTable()[4][1]);
        assertEquals(0, test.getPathTable()[5][1]);
    }

    @Test
    void determinePathTest(){
        PhysicalNetwork pNtwk = new PhysicalNetwork("pt6");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }

        DijkstrasRoutingAlgorithm test = new DijkstrasRoutingAlgorithm(pNtwk);
        test.routeTraffic(0, 3);
        int[] path = test.getPath();

        //the path should be [0, 1, 2, 3] for this case (worked out on paper)
        assertEquals(0, path[0]);
        assertEquals(1, path[1]);
        assertEquals(2, path[2]);
        assertEquals(3, path[3]);
    }

    @Test
    void determineSlotTest(){
        PhysicalNetwork pNtwk = new PhysicalNetwork("pt6");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }

        DijkstrasRoutingAlgorithm test = new DijkstrasRoutingAlgorithm(pNtwk);
        test.routeTraffic(0, 3);
        int[] slots = test.getSlots();
        int[] expected = {0};
        assertArrayEquals(expected, slots);

        //Mark resources as taken on slot 0. Do this via a Connection??
        Connection first = new Connection();
        first.setSlotsUsed(slots);
        first.setPath(test.getPath());
        first.claimResources(pNtwk);

        //route more traffic from 0 to 3.
        DijkstrasRoutingAlgorithm test2 = new DijkstrasRoutingAlgorithm(pNtwk);
        test2.routeTraffic(0, 3);
        slots = test2.getSlots();
        expected[0] = 1;
        assertArrayEquals(expected, slots); //assert that slots == {1};

        //Mark resources as taken on entire network
        //Try to route traffic from 0 to 3. How should this be handled?


    }
}
