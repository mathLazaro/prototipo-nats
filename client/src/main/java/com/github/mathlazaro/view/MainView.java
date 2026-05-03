package com.github.mathlazaro.view;

import com.github.mathlazaro.model.main.MainOperation;

import static com.github.mathlazaro.MainClient.IN;

public class MainView implements View {

    public MainOperation readMainOperation() {
        try {
            System.out.println("Escolha a operação (1 a 3):");
            System.out.println("1. Cálculo de distância (flores)");
            System.out.println("2. Mensagens");
            System.out.println("3. Gerenciamento de arquivos");
            System.out.print("Opção: ");
            int option = IN.nextInt();
            IN.nextLine();

            System.out.printf("%n");

            return switch (option) {
                case 1 -> MainOperation.FLOWER_CALCULATION;
                case 2 -> MainOperation.MESSAGE;
                case 3 -> MainOperation.FILE;
                default -> throw new IllegalArgumentException("Opção inválida");
            };
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + " Tente novamente.");
            return readMainOperation();
        } catch (Exception e) {
            System.err.println("Erro ao ler a opção de operação. Tente novamente.");
            throw e;
        }
    }


}
