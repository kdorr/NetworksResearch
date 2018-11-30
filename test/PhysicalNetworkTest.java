import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PhysicalNetworkTest {

    @Test
    void setUpNetwork(){
        PhysicalNetwork ntwk = new PhysicalNetwork("pt6");
        try {
            ntwk.createNetwork();
        } catch (IOException e){
            System.err.println("IO Execption from reading the network caught");
        }
        System.out.println(ntwk.toString());
    }
}
