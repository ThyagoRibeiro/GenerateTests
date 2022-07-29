
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileUtilTest {

    @Test
    public void test() throws IOException {

        String filePath = getClass().getResource("input/smartphone_transition_table.csv").getPath();

        List<List<String>> lists = FileUtil.readCSVFile(filePath, 0);

        assertEquals("states/events", lists.get(0).get(0));
        assertEquals("home", lists.get(0).get(1));
        assertEquals(" LOCKED", lists.get(1).get(1));
        assertEquals("OFF", lists.get(3).get(5));

    }

}
