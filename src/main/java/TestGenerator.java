import java.util.*;

public class TestGenerator {

    private StateMachine stateMachine;

    private Map<String, String> eventActionMap;
    private Map<State, Map<String, State>> basisTransitionTree;
    private Map<State, Map<String, State>> secretTransitionTree;
    private List<List<Map<String, State>>> basisPaths;
    private List<List<Map<String, State>>> secretPaths;
    Map<State, Integer> stateFrequency = new HashMap<>();
    private String className;

    private static final String ASSERT_EQUALS_LINE = "\t\tassertEquals(\"%s\", %s);";
    private static final String GET_STATE_METHOD = "%s.getState()";

    TestGenerator(List<List<String>> transitionTable, List<List<String>> eventActionTable) {

        this.stateMachine = new StateMachine(transitionTable);
        readEventActionTable(eventActionTable);
        this.basisTransitionTree = mountTransitionTree(stateMachine.getBasisTransitions());
        this.secretTransitionTree = mountTransitionTree(stateMachine.getSecretTransitions());
        calculateBasisPaths();
        calculateSecretPaths();

    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    private void readEventActionTable(List<List<String>> eventActionTable) {

        for (List<String> lines : eventActionTable) {
            lines.replaceAll(String::trim);
        }

        this.eventActionMap = new HashMap<>();
        className = eventActionTable.remove(0).get(1);
        eventActionTable.forEach(eventAction -> this.eventActionMap.put(eventAction.get(0), eventAction.get(1)));
    }

    private Map mountTransitionTree(TransitionMap transitionMap) {

        Map<State, Map<String, State>> transitionTree = new LinkedHashMap();

        for(Map.Entry<State, Map<String, State>> entry : transitionMap.getTransitions().entrySet()) {
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

        return transitionTree;

    }

    private void calculateBasisPaths() {

        basisPaths = new ArrayList<>();
        State firstState = basisTransitionTree.keySet().iterator().next();
        calculatePaths(new ArrayList(List.of(Map.of("", firstState))), new ArrayList(), basisTransitionTree, basisPaths);

    }

    private void calculateSecretPaths() {

        secretPaths = new ArrayList<>();
        State firstState = secretTransitionTree.keySet().iterator().next();

        Map<State, Map<String, State>> transitionTree = new HashMap<>();

        for (State state : basisTransitionTree.keySet()) {

            transitionTree.put(state, new HashMap<>());
            Map<String, State> eventStateMap = basisTransitionTree.get(state);

            for (String event : eventStateMap.keySet()) {
                transitionTree.get(state).put(event, eventStateMap.get(event));
            }

        }

        for (State state : secretTransitionTree.keySet()) {
            transitionTree.get(state).putAll(secretTransitionTree.get(state));
        }

        calculatePaths(new ArrayList(List.of(Map.of("", firstState))), new ArrayList(), transitionTree, secretPaths);

        for (List<Map<String, State>> basisPath : basisPaths) {
            secretPaths.remove(basisPath);
        }


    }

    private void calculatePaths(List<Map<String, State>> currPath, List<State> visitatedStateList, Map<State, Map<String, State>> transitionTree, List<List<Map<String, State>>> paths) {

        State currState = currPath.get(currPath.size() - 1).values().iterator().next();
        visitatedStateList.add(new State(currState.getLabel().replaceAll("_.", "")));

        for(Map.Entry<String, State> entry : transitionTree.get(currState).entrySet()) {
            State nextState = entry.getValue();

            Map<String, State> nextTransition = Map.of(entry.getKey(), nextState);

            currPath.add(nextTransition);

            if(!visitatedStateList.contains(new State(nextState.getLabel().replaceAll("_.", "")))) {
                calculatePaths(currPath, visitatedStateList, transitionTree, paths);
            } else {
                paths.add(new ArrayList(currPath));
            }
            currPath.remove(nextTransition);

        }

    }

    public String getBasisTransitionTree() {
        return getTransitionTree(basisTransitionTree);
    }

    public String getSecretTransitionTree() {
        return getTransitionTree(secretTransitionTree);
    }

    private String getTransitionTree(Map<State, Map<String, State>> transitionTree) {

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
        return getPaths(basisPaths);
    }

    public String getSecretPaths() {

        List<List<Map<String, State>>> paths = new ArrayList<>(basisPaths);


        return getPaths(secretPaths);
    }

    private String getPaths(List<List<Map<String, State>>> paths) {

        StringBuffer sb = new StringBuffer();
        for(List<Map<String, State>> basicPath : paths) {
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

        pathsToCodeTest(singleTestTemplate, sbTests, getStateClassMethod, basisPaths, "testaCaminhoBasico");
        pathsToCodeTest(singleTestTemplate, sbTests, getStateClassMethod, secretPaths, "testaCaminhoSecreto");

        sbTests.deleteCharAt(sbTests.lastIndexOf("\n"));
        sbTests.deleteCharAt(sbTests.lastIndexOf("\n"));

        String templateFilePath = getClass().getResource("script_test_template.txt").getPath();
        String testScript = FileUtil.readFile(templateFilePath);

        String variableName = className.toLowerCase(Locale.ROOT);

        testScript = String.format(testScript, className, className, variableName, variableName, className, sbTests);

        return testScript;

    }

    private void pathsToCodeTest(String singleTestTemplate, StringBuffer sbTests, String getStateClassMethod, List<List<Map<String, State>>> paths, String testPrefix) {

        for (int i = 0; i < paths.size(); i++) {

            StringBuffer sbSingleTest = new StringBuffer();

            for (Map<String, State> transition : paths.get(i)) {

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

            sbTests.append(String.format(singleTestTemplate, String.format("%s%02d", testPrefix,i + 1), sbSingleTest.toString()));
            sbTests.append("\n");

        }

    }
}
