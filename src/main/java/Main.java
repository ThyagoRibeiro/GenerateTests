import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static StateMachine stateMachine;

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Universidade de São Paulo");
        System.out.println("Escola de Artes, Ciências e Humanidades");
        System.out.println("Programa de Pós-graduação em Sistemas de Informação");
        System.out.println("Disciplina de Teste de Software - SIN5022");

        do {

            System.out.println("\n----------\n");

            System.out.print("Digite o caminho da tabela de transição (csv): ");
            List<List<String>> transitionTable = FileUtil.readCSVFile(scanner.nextLine(), 0);

            System.out.print("Digite o caminho do mapeamento de eventos/ações (csv): ");
            List<List<String>> eventActionTable = FileUtil.readCSVFile(scanner.nextLine(), 2);

            StateMachine stateMachine = new StateMachine(transitionTable, eventActionTable);

            System.out.println("----------\n");

            System.out.println("Árvore de transição:\n");
            System.out.println(stateMachine.getTransitionTree());

            System.out.println("\n----------\n");

            System.out.println("Caminhos básicos:\n");
            System.out.println(stateMachine.getBasisPaths());

            System.out.println("\n----------\n");

            System.out.println("Script de teste:\n");
            System.out.println(stateMachine.getTestScript());

            System.out.println("----------\n");

            System.out.print("Gostaria de executar novamente? (S/N): ");

        } while (scanner.nextLine().toUpperCase().equals("S"));

        System.out.println("Finalizando gerador de testes. Obrigado.");

        scanner.close();

    }

}
