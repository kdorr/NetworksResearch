import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionTest {
    @Test
    void claimResourcesTest(){
        PhysicalNetwork pn = new PhysicalNetwork("pt6Simple");
        try {
            pn.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }
        System.out.println("Initial:\n" + pn);
        Connection c1 = new Connection();
        int[] path = {1, 5, 4};
        int[] slots = {1, 2};
        c1.setPath(path);
        c1.setSlotsUsed(slots);
        c1.claimResources(pn);
        System.out.println("After claimed:\n" + pn);

        Edge[][] ntwk = pn.getNetwork();
        for(int r=1; r<path.length; r++){
            for(int s=0; s<slots.length; s++) {
                assertTrue(ntwk[path[r - 1]][path[r]].getSlots()[slots[s]]);
                assertTrue(ntwk[path[r]][path[r - 1]].getSlots()[slots[s]]);
            }
        }
    }

    @Test
    /**
     * Test for the mechanism that releases resources. A little messy.
     * Uses code from the above test to claim resources and then releases them.
     */
    void releaseResourcesTest(){
        PhysicalNetwork pn = new PhysicalNetwork("pt6Simple");
        try {
            pn.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }
        System.out.println("Initial:\n" + pn);
        Connection c1 = new Connection();
        int[] path = {1, 5, 4};
        int[] slots = {1, 2};
        c1.setPath(path);
        c1.setSlotsUsed(slots);
        c1.claimResources(pn);
        System.out.println("After claimed:\n" + pn);

        Edge[][] ntwk = pn.getNetwork();
        for(int r=1; r<path.length; r++){
            for(int s=0; s<slots.length; s++) {
                assertTrue(ntwk[path[r - 1]][path[r]].getSlots()[slots[s]]);
                assertTrue(ntwk[path[r]][path[r - 1]].getSlots()[slots[s]]);
            }
        }

        c1.releaseResources(pn);
        System.out.println("After released:\n" + pn);

        ntwk = pn.getNetwork();
        for(int r=1; r<path.length; r++){
            for(int s=0; s<slots.length; s++) {
                assertFalse(ntwk[path[r - 1]][path[r]].getSlots()[slots[s]]);
                assertFalse(ntwk[path[r]][path[r - 1]].getSlots()[slots[s]]);
            }
        }
    }
}
