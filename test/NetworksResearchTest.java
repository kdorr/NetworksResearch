import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworksResearchTest {
    @Test
    /**
     * Tests IO. Note: very hacky since append. (Need to manually clear file)
     */
    void writeToFile(){
        NetworksResearch.writeToFile("foo.csv", "1, 2, 3, 4");
        NetworksResearch.writeToFile("foo.csv", "5, 6, 7, 8");
        String in = "";
        try(BufferedReader reader = Files.newBufferedReader(Paths.get("foo.csv"), StandardCharsets.UTF_8)){
            String line = null;
            while((line = reader.readLine()) != null){
                //System.out.println(line);
                in += (line+"\n");
            }
        } catch( IOException x){
            System.err.format("IOException: %s%n", x);
        }

        assertEquals("1, 2, 3, 4\n5, 6, 7, 8\n", in);
    }
}