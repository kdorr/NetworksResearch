import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {
    @Test
    void initBandwidthSlots(){
        Edge test = new Edge(32, 300, 1, 0);
        System.out.println(test.toString());
    }
}
