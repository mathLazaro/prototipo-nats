package com.github.mathlazaro.view;

import com.github.mathlazaro.model.message.JokeType;
import com.github.mathlazaro.model.message.MessageOperation;

import static com.github.mathlazaro.MainClient.IN;

public class MessageView implements View {

    private static MessageView INSTANCE;

    private MessageView() {

    }

    public static MessageView getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageView();
        }
        return INSTANCE;
    }

    public MessageOperation readOperation() {
        try {
            System.out.println("Escolha a operação (1 ou 2):");
            System.out.println("1. Saudações");
            System.out.println("2. Piadas");
            System.out.print("Opção: ");
            int option = IN.nextInt();
            IN.nextLine();

            System.out.printf("%n");

            return switch (option) {
                case 1 -> MessageOperation.GREETINGS;
                case 2 -> MessageOperation.JOKE;
                default -> throw new IllegalArgumentException("Opção inválida");
            };
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + " Tente novamente.");
            return readOperation();
        } catch (Exception e) {
            System.err.println("Erro ao ler a opção de operação. Tente novamente.");
            throw e;
        }
    }

    public String readMessage() {
        System.out.print("Mensagem:");
        return IN.nextLine();
    }

    public JokeType readJokeType() {
        try {
            System.out.println("Escolha o tipo de piada (1 a 5):");
            System.out.println("1. Geral");
            System.out.println("2. Bate-bate");
            System.out.println("3. Programação");
            System.out.println("4. Pai");
            System.out.println("5. Variado");
            System.out.print("Opção: ");
            int option = IN.nextInt();
            IN.nextLine();

            System.out.printf("%n");

            return switch (option) {
                case 1 -> JokeType.GENERAL;
                case 2 -> JokeType.KNOCK_KNOCK;
                case 3 -> JokeType.PROGRAMMING;
                case 4 -> JokeType.DAD;
                case 5 -> JokeType.ANY;
                default -> throw new IllegalArgumentException("Opção inválida");
            };
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + " Tente novamente.");
            return readJokeType();
        } catch (Exception e) {
            System.err.println("Erro ao ler a opção de tipo de piada. Tente novamente.");
            throw e;
        }
    }

    public void displayJokeResponse(String joke) {
        System.out.println("Piada: " + joke);
    }

    public void displayGreetingResponse(String greeting) {
        System.out.println("Resposta: " + greeting);
    }

}
