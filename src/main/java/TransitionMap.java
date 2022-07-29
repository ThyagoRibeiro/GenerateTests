import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TransitionMap {

    Map<State, Map<String, State>> transitions;

    public TransitionMap() {
        this.transitions = new LinkedHashMap<State, Map<String, State>>();;
    }

    void addState(String label) {
        transitions.putIfAbsent(new State(label), new HashMap());
    }

    void addTransition(String label1, String event, String label2) {
        State s1 = new State(label1);
        State s2 = new State(label2);
        transitions.get(s1).put(event, s2);
    }

    public Map<State, Map<String, State>> getTransitions() {
        return transitions;
    }

    public State getAdjStates(String stateLabel, String eventLabel) {
        return transitions.get(new State(stateLabel)).get(eventLabel);
    }

}
