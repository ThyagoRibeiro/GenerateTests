import java.util.*;

public class StateMachine {

    private TransitionMap basisTransitions;
    private TransitionMap secretTransitions;
    private List<String> events;

    public StateMachine(List<List<String>> transitionTable) {

        for (List<String> lines : transitionTable) {
            lines.replaceAll(String::trim);
        }

        this.events = new ArrayList(transitionTable.remove(0));
        this.events.remove(0);

        this.basisTransitions = new TransitionMap();
        this.secretTransitions = new TransitionMap();

        for (List<String> transitionNames : transitionTable) {
            basisTransitions.addState(transitionNames.get(0));
            secretTransitions.addState(transitionNames.get(0));
            for (int i = 1; i < transitionNames.size(); i++) {
                if (!transitionNames.get(i).equals("")) {
                    basisTransitions.addTransition(transitionNames.get(0), this.events.get(i - 1), transitionNames.get(i));
                } else {
                    secretTransitions.addTransition(transitionNames.get(0), this.events.get(i - 1), transitionNames.get(0));
                }
            }
        }

    }

    public List<String> getEvents() {
        return events;
    }

    public TransitionMap getBasisTransitions() {
        return basisTransitions;
    }

    public TransitionMap getSecretTransitions() {
        return secretTransitions;
    }
}
