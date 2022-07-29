import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGeneratorTest {

    public static List<List<String>> transitionTable;
    public static List<List<String>> eventActionTable;

    @Before
    public void init() throws IOException {

        transitionTable = new ArrayList<>();
        eventActionTable = new ArrayList<>();

        transitionTable.add(new ArrayList<String>(Arrays.asList("states/events", "home", " wrong password", " correct password", "lock button", " long lock button")));
        transitionTable.add(new ArrayList<String>(Arrays.asList("OFF", "LOCKED", "", "", "", "")));
        transitionTable.add(new ArrayList<String>(Arrays.asList("LOCKED", "", "LOCKED", "UNLOCKED", "", "OFF")));
        transitionTable.add(new ArrayList<String>(Arrays.asList("UNLOCKED", "", "", "", "LOCKED", "OFF")));

        eventActionTable.add(new ArrayList<String>(Arrays.asList("system proxy", " SDevice")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("home", " SDevice.home()")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("wrong password", " SDevice.login(“wrong”,”wrong”)")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("correct password", " SDevice.login(“login”,”password”)")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("lock button", " SDevice.lockButton()")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("long lock button", " SDevice.longLockButton()")));

    }

    @Test
    public void test_mount_state_machine() throws IOException {

        TestGenerator testGenerator = new TestGenerator(transitionTable, eventActionTable);

        assertEquals(5, testGenerator.getStateMachine().getEvents().size());
        assertEquals(3, testGenerator.getStateMachine().getBasisTransitions().getTransitions().size());
        assertTrue(testGenerator.getStateMachine().getBasisTransitions().getAdjStates("OFF", "home").getLabel().startsWith("LOCKED"));
        assertTrue(testGenerator.getStateMachine().getBasisTransitions().getAdjStates("LOCKED", "correct password").getLabel().startsWith("UNLOCKED"));
        assertTrue(testGenerator.getStateMachine().getBasisTransitions().getAdjStates("UNLOCKED", "long lock button").getLabel().startsWith("OFF"));

    }

    @Test
    public void test_print_basis_transition_tree() throws IOException {

        String filePath = getClass().getResource("output/basis_transition_tree.txt").getPath();
        String expected = readFile(filePath).trim().replace("\r","");

        TestGenerator stateMachine = new TestGenerator(transitionTable, eventActionTable);

        assertEquals(expected, stateMachine.getBasisTransitionTree().trim().replace("\r",""));

    }

    @Test
    public void test_print_secret_transition_tree() throws IOException {

        String filePath = getClass().getResource("output/secret_transition_tree.txt").getPath();
        String expected = readFile(filePath).trim().replace("\r","");

        TestGenerator stateMachine = new TestGenerator(transitionTable, eventActionTable);

        assertEquals(expected, stateMachine.getSecretTransitionTree().trim().replace("\r",""));

    }

    @Test
    public void test_print_basis_paths() throws IOException {

        String filePath = getClass().getResource("output/basis_paths.txt").getPath();
        String expected = readFile(filePath).trim().replace("\r","");

        TestGenerator stateMachine = new TestGenerator(transitionTable, eventActionTable);

        assertEquals(expected, stateMachine.getBasisPaths().trim().replace("\r",""));

    }

    @Test
    public void test_print_secret_paths() throws IOException {

        String filePath = getClass().getResource("output/secret_paths.txt").getPath();
        String expected = readFile(filePath).trim().replace("\r","");

        TestGenerator stateMachine = new TestGenerator(transitionTable, eventActionTable);

        assertEquals(expected, stateMachine.getSecretPaths().trim().replace("\r",""));

    }

    @Test
    public void test_print_script_test() throws IOException {

        String filePath = getClass().getResource("output/generated_code.txt").getPath();
        String expected = readFile(filePath).replace("\r","");

        TestGenerator stateMachine = new TestGenerator(transitionTable, eventActionTable);

        assertEquals(expected, stateMachine.getTestScript());

    }

    private String readFile(String filePath) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }

    }
}
