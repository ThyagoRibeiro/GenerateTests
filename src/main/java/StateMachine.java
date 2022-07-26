import java.util.*;

public class StateMachine {

    private List<String> events;
    private Map<State, Map<String, State>> states;
    private Map<String, String> eventActionMap;
    private Map<State, Map<String, State>> transitionTree;
    private List<List<Map<String, State>>> basisPaths;
    private String className;

    private static final String ASSERT_EQUALS_LINE = "\t\tassertEquals(\"%s\", %s);";
    private static final String GET_STATE_METHOD = "%s.getState()";

    StateMachine(List<List<String>> transitionTable, List<List<String>> eventActionTable) {

        readTransitionTable(transitionTable);
        readEventActionTable(eventActionTable);
        mountTransitionTree();
        calculateBasisPaths();

    }

    private void readTransitionTable(List<List<String>> transitionTable) {

        for (List<String> lines : transitionTable) {
            lines.replaceAll(String::trim);
        }

        this.events = new ArrayList(transitionTable.remove(0));
        this.events.remove(0);

        this.states = new LinkedHashMap<State, Map<String, State>>();

        for (List<String> transition : transitionTable) {
            addState(transition.get(0));
            for(int i = 1; i < transition.size(); i++) {
                if(!transition.get(i).equals("")) {
                    addTransition(transition.get(0), this.events.get(i - 1), transition.get(i));
                }
            }
        }

    }

    private void readEventActionTable(List<List<String>> eventActionTable) {

        for (List<String> lines : eventActionTable) {
            lines.replaceAll(String::trim);
        }

        this.eventActionMap = new HashMap<>();
        className = eventActionTable.remove(0).get(1);
        eventActionTable.forEach(eventAction -> this.eventActionMap.put(eventAction.get(0), eventAction.get(1)));
    }

    private void mountTransitionTree() {

        Map<State, Integer> stateFrequency = new HashMap<>();
        transitionTree = new LinkedHashMap();

        for(Map.Entry<State, Map<String, State>> entry : states.entrySet()) {
            transitionTree.put(new State(entry.getKey().getLabel() + "_0"), entry.getValue());
        }

        for(State s1 : transitionTree.keySet()) {
            stateFrequency.putIfAbsent(new State(s1.getLabel().replaceAll("_0", "")), 0);
            for (String event : transitionTree.get(s1).keySet()) {
                State s2 = transitionTree.get(s1).get(event);
                stateFrequency.putIfAbsent(s2, -1);
                stateFrequency.put(s2, stateFrequency.get(s2) + 1);
                transitionTree.get(s1).put(event, new State(s2 + "_" + stateFrequency.get(s2)));
            }
        }

    }

    private void calculateBasisPaths() {

        basisPaths = new ArrayList<>();
        State firstState = transitionTree.keySet().iterator().next();

        calculateBasisPaths(new ArrayList(List.of(Map.of("", firstState))), new ArrayList());
        System.out.println();

    }

    private void calculateBasisPaths(List<Map<String, State>> currPath, List<State> visitatedStateList) {

        State currState = currPath.get(currPath.size() - 1).values().iterator().next();
        visitatedStateList.add(new State(currState.getLabel().replaceAll("_.", "")));

        for(Map.Entry<String, State> entry : transitionTree.get(currState).entrySet()) {
            State nextState = entry.getValue();

            Map<String, State> nextTransition = Map.of(entry.getKey(), nextState);

            currPath.add(nextTransition);

            if(!visitatedStateList.contains(new State(nextState.getLabel().replaceAll("_.", "")))) {
                calculateBasisPaths(currPath, visitatedStateList);
            } else {
                basisPaths.add(new ArrayList(currPath));
                currPath.remove(nextTransition);
            }

        }

    }

    void addState(String label) {
        states.putIfAbsent(new State(label), new HashMap());
    }

    void addTransition(String label1, String event, String label2) {
        State s1 = new State(label1);
        State s2 = new State(label2);
        states.get(s1).put(event, s2);
    }

    public List<String> getEvents() {
        return events;
    }

    public Map<State, Map<String, State>> getStates() {
        return states;
    }

    public State getAdjStates(String stateLabel, String eventLabel) {
        return states.get(new State(stateLabel)).get(eventLabel);
    }

    public String getTransitionTree() {

        StringBuffer sb = new StringBuffer();
        for(State s1 : transitionTree.keySet()) {
            for (State s2 : transitionTree.get(s1).values()) {
                sb.append(s1);
                sb.append(", ");
                sb.append(s2);
                sb.append("\n");
            }
        }

        sb.deleteCharAt(sb.lastIndexOf("\n"));

        return sb.toString();

    }

    public String getBasisPaths() {

        StringBuffer sb = new StringBuffer();
        for(List<Map<String, State>> basicPath : basisPaths) {
            for (Map<String, State> transition : basicPath) {
                sb.append(transition.values().iterator().next());
                sb.append(", ");
            }
            sb.deleteCharAt(sb.lastIndexOf(", "));
            sb.append("\n");
        }

        sb.deleteCharAt(sb.lastIndexOf("\n"));

        return sb.toString();

    }

    public String getTestScript() {

        String singleTestFilePath = getClass().getResource("single_test_template.txt").getPath();
        String singleTestTemplate = FileUtil.readFile(singleTestFilePath);

        StringBuffer sbTests = new StringBuffer();
        String getStateClassMethod = String.format(GET_STATE_METHOD, className);

        for (int i = 0; i < basisPaths.size(); i++) {

            StringBuffer sbSingleTest = new StringBuffer();

            for (Map<String, State> transition : basisPaths.get(i)) {

                String event = eventActionMap.get(transition.keySet().iterator().next());

                if(event != null) {
                    sbSingleTest.append("\t\t");
                    sbSingleTest.append(event);
                    sbSingleTest.append(";");
                    sbSingleTest.append("\n");
                }

                sbSingleTest.append(String.format(ASSERT_EQUALS_LINE, transition.values().iterator().next().getLabel().replaceAll("_.", ""), getStateClassMethod));
                sbSingleTest.append("\n\n");

            }

            sbSingleTest.deleteCharAt(sbSingleTest.lastIndexOf("\n"));
            sbSingleTest.deleteCharAt(sbSingleTest.lastIndexOf("\n"));

            sbTests.append(String.format(singleTestTemplate, String.format("testaCaminho%02d", i + 1), sbSingleTest.toString()));
            sbTests.append("\n");

        }

        sbTests.deleteCharAt(sbTests.lastIndexOf("\n"));
        sbTests.deleteCharAt(sbTests.lastIndexOf("\n"));

        String templateFilePath = getClass().getResource("script_test_template.txt").getPath();
        String testScript = FileUtil.readFile(templateFilePath);

        String variableName = className.toLowerCase(Locale.ROOT);

        testScript = String.format(testScript, className, className, variableName, variableName, className, sbTests);

        return testScript;

    }
}
