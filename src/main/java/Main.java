import java.io.IOException;
import java.util.List;

public class Main {

    private static StateMachine stateMachine;

    public static void main(String[] args) throws IOException {

        List<List<String>> transitionTable = FileUtil.readCSVFile(args[0], 0);
        List<List<String>> eventActionTable = FileUtil.readCSVFile(args[1], 2);
        StateMachine stateMachine = new StateMachine(transitionTable, eventActionTable);

        stateMachine.printTransitionTree();
        stateMachine.printBasisPath();
        stateMachine.printTestScript();

    }

}
