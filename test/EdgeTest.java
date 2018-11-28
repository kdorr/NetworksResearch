import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {
    @Test
    void initBandwidthSlots(){
        Edge test = new Edge(32, 300, 1, 0);
        assertEquals(32, test.getSlots().length);
        for(int i=0; i<test.getSlots().length; i++){
            assertEquals(0, test.getSlots()[i]);
        }
    }

    @Test
    void initSrcDest(){
        Edge test = new Edge(32, 300, 1, 0);
        assertEquals(1, test.getSrcNode());
        assertEquals(0, test.getDestNode());
    }

    @Test
    void initDist(){
        Edge test = new Edge(32, 300, 1, 0);
        assertEquals(300, test.getDistance());
    }
}
