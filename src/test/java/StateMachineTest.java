import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateMachineTest {

    public static final List<List<String>> transitionTable = new ArrayList<>();
    public static final List<List<String>> eventActionTable = new ArrayList<>();

    @Before
    public void init() throws IOException {

        transitionTable.add(new ArrayList<String>(Arrays.asList("states/events","home"," wrong password"," correct password","lock button"," long lock button")));
        transitionTable.add(new ArrayList<String>(Arrays.asList("OFF","LOCKED")));
        transitionTable.add(new ArrayList<String>(Arrays.asList("LOCKED","","LOCKED","UNLOCKED","","OFF")));
        transitionTable.add(new ArrayList<String>(Arrays.asList("UNLOCKED","","","","LOCKED","OFF")));

        eventActionTable.add(new ArrayList<String>(Arrays.asList("system proxy"," SDevice")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("home"," SDevice.home()")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("wrong password"," SDevice.login(“wrong”,”wrong”)")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("correct password"," SDevice.login(“login”,”password”)")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("lock button"," SDevice.lockButton()")));
        eventActionTable.add(new ArrayList<String>(Arrays.asList("long lock button"," SDevice.longLockButton()")));

    }

    //@Test
    //public void test_mount_state_machine() throws IOException {
//
    //    StateMachine stateMachine = new StateMachine(transitionTable, eventActionTable);
    //    assertEquals(5, stateMachine.getEvents().size());
    //    assertEquals(3, stateMachine.getStates().size());
    //    assertTrue(stateMachine.getAdjStates("OFF", "home").getLabel().startsWith("LOCKED"));
    //    assertTrue(stateMachine.getAdjStates("LOCKED", "correct password").getLabel().startsWith("UNLOCKED"));
    //    assertTrue(stateMachine.getAdjStates("UNLOCKED", "long lock button").getLabel().startsWith("OFF"));
//
    //}
//
    //@Test
    //public void test_print_transition_tree() throws IOException {
//
    //    StateMachine stateMachine = new StateMachine(transitionTable, eventActionTable);
    //    System.out.println(stateMachine.printTransitionTree());
//
    //}
//
    //@Test
    //public void test_print_basis_path() throws IOException {
//
    //    StateMachine stateMachine = new StateMachine(transitionTable, eventActionTable);
    //    System.out.println(stateMachine.printBasisPath());
//
    //}

    @Test
    public void test_print_script_test() throws IOException {

        StateMachine stateMachine = new StateMachine(transitionTable, eventActionTable);
        System.out.println(stateMachine.printTestScript());

    }

}
