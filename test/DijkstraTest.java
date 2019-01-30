import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

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
        int[] path = test.determinePath(0, 3);

        //the path should be [0, 1, 2, 3] for this case (worked out on paper)
        assertEquals(0, path[0]);
        assertEquals(1, path[1]);
        assertEquals(2, path[2]);
        assertEquals(3, path[3]);
    }
}
