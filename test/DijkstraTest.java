import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(1500, test.getPathTable()[2][0]);
        assertEquals(2100, test.getPathTable()[3][0]);
        assertEquals(1500, test.getPathTable()[4][0]);
        assertEquals(500, test.getPathTable()[5][0]);
        //Test predecessors (expected worked out on paper)
        assertEquals(0, test.getPathTable()[0][1]);
        assertEquals(0, test.getPathTable()[1][1]);
        assertEquals(1, test.getPathTable()[2][1]);
        assertEquals(2, test.getPathTable()[3][1]); // Could also be 4 depending on tie break
        assertEquals(5, test.getPathTable()[4][1]);
        assertEquals(0, test.getPathTable()[5][1]);
    }

    @Test
    void determinePathTest(){
        PhysicalNetwork pNtwk = new PhysicalNetwork("ptDebug");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }

        DijkstrasRoutingAlgorithm test = new DijkstrasRoutingAlgorithm(pNtwk);
        test.routeTraffic(3, 0, 2);
        int[] path = test.getPath();

        //the path should be [0, 1, 2, 3] for this case (worked out on paper)
        assertEquals(3, path[0]);
        assertEquals(2, path[1]);
        assertEquals(1, path[2]);
        assertEquals(0, path[3]);
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
        test.routeTraffic(0, 3, 2);
        int[] slots = test.getSlots();
        int[] expected = {0};
        assertArrayEquals(expected, slots);

        //Mark resources as taken on slot 0. Do this via a Connection
        Connection first = new Connection();
        Connection end = new Connection();
        first.setOther(end);
        first.setSlotsUsed(slots);
        first.setPath(test.getPath());
        first.claimResources(pNtwk);

        //route more traffic from 0 to 3.
        DijkstrasRoutingAlgorithm test2 = new DijkstrasRoutingAlgorithm(pNtwk);
        test2.routeTraffic(0, 3,2);
        slots = test2.getSlots();
        expected[0] = 1;
        assertArrayEquals(expected, slots); //assert that slots == {1};

        //Mark resources as taken on slot 1. Do this via a Connection
        Connection second = new Connection();
        end = new Connection();
        second.setOther(end);
        second.setSlotsUsed(slots);
        second.setPath(test.getPath());
        second.claimResources(pNtwk);

        //Mark resources as taken on all but the last slot on network
        int[] mostSlots = new int[29];
        for(int i=0; i<29; i++){
            mostSlots[i] = i+2;
        }

        for(int i=0; i<test2.getPath().length-1; i++){
            pNtwk.getNetwork()[test2.getPath()[i]][test2.getPath()[i+1]].markRangeTaken(mostSlots);
            pNtwk.getNetwork()[test2.getPath()[i+1]][test2.getPath()[i]].markRangeTaken(mostSlots);
        }

        //Checking edge case: routing on last available slot
        DijkstrasRoutingAlgorithm test3 = new DijkstrasRoutingAlgorithm(pNtwk);
        test3.routeTraffic(0, 3,2);
        slots = test3.getSlots();
        expected[0] = 31;
        assertArrayEquals(expected, slots); //assert that slots == {1};

    }

    @Test
    void rejectConnectionTest(){
        PhysicalNetwork pNtwk = new PhysicalNetwork("pt6");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }

        //Mark resources as taken on all slots on network
        int[] mostSlots = new int[32];
        for(int i=0; i<32; i++){
            mostSlots[i] = i;
        }

        DijkstrasRoutingAlgorithm test = new DijkstrasRoutingAlgorithm(pNtwk);
        test.routeTraffic(0, 3,2);
        int[] slots = test.getSlots();
        int[] expected = {0};
        assertArrayEquals(expected, slots); //make sure the path routed correctly

        // manually mark all slots along the path as taken
        for(int i=0; i<test.getPath().length-1; i++){
            pNtwk.getNetwork()[test.getPath()[i]][test.getPath()[i+1]].markRangeTaken(mostSlots);
            pNtwk.getNetwork()[test.getPath()[i+1]][test.getPath()[i]].markRangeTaken(mostSlots);;
        }

        DijkstrasRoutingAlgorithm test2 = new DijkstrasRoutingAlgorithm(pNtwk);
        assertFalse(test2.routeTraffic(0,3,2));
    }

    @Test
    void determineRejectSlotTest(){
        PhysicalNetwork pNtwk = new PhysicalNetwork("pt6");
        try {
            pNtwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }

        DijkstrasRoutingAlgorithm test = new DijkstrasRoutingAlgorithm(pNtwk);
        test.routeTraffic(0, 3,2);
        int[] slots = test.getSlots();
        int[] expected = {0};
        assertArrayEquals(expected, slots);

        //Mark resources as taken on slot 0. Do this via a Connection
        Connection first = new Connection();
        Connection end = new Connection();
        first.setOther(end);
        first.setSlotsUsed(slots);
        first.setPath(test.getPath());
        first.claimResources(pNtwk);

        DijkstrasRoutingAlgorithm test3;
        Connection rest;
        for(int i=1; i<32; i++){
            test3 = new DijkstrasRoutingAlgorithm(pNtwk);
            assertTrue(test3.routeTraffic(0,3,2));
            slots = test3.getSlots();
            expected[0] = i;
            assertArrayEquals(expected, slots);

            rest = new Connection();
            rest.setOther(end);
            rest.setSlotsUsed(slots);
            rest.setPath(test.getPath());
            rest.claimResources(pNtwk);
        }

        test3 = new DijkstrasRoutingAlgorithm(pNtwk);
        assertFalse(test3.routeTraffic(0,3,2));

    }
}
