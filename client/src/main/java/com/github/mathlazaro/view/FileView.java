package com.github.mathlazaro.view;

import com.github.mathlazaro.model.file.FileOperation;

import static com.github.mathlazaro.MainClient.IN;

public class FileView implements View {

    private static FileView INSTANCE;

    private FileView() {

    }

    public static FileView getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileView();
        }
        return INSTANCE;
    }

    public FileOperation readOperation() {
        try {
            System.out.println("Escolha a operação com arquivo (1 a 4):");
            System.out.println("1. Salvar");
            System.out.println("2. Ler");
            System.out.println("3. Adicionar dados");
            System.out.println("4. Deletar");
            System.out.print("Opção: ");
            int option = IN.nextInt();
            IN.nextLine();

            System.out.printf("%n");

            return switch (option) {
                case 1 -> FileOperation.SAVE;
                case 2 -> FileOperation.READ;
                case 3 -> FileOperation.APPEND;
                case 4 -> FileOperation.DELETE;
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

    public String readFileName() {
        System.out.print("Nome do arquivo: ");
        return IN.nextLine();
    }

    public byte[] readFileData() {
        System.out.print("Dados (como texto): ");
        String data = IN.nextLine();
        return data.getBytes();
    }

    public void displaySaveSuccess(String filename) {
        System.out.println("Arquivo '" + filename + "' salvo com sucesso.");
    }

    public void displayReadSuccess(byte[] data) {
        if (data != null && data.length > 0) {
            System.out.println("Conteúdo do arquivo: " + new String(data));
        } else {
            System.out.println("Arquivo vazio ou não encontrado.");
        }
    }

    public void displayAppendSuccess(String filename) {
        System.out.println("Dados adicionados ao arquivo '" + filename + "' com sucesso.");
    }

    public void displayDeleteSuccess(String filename) {
        System.out.println("Arquivo '" + filename + "' deletado com sucesso.");
    }

    public void displayError(String message) {
        System.err.println("Erro ao executar operação: " + message);
    }

}
