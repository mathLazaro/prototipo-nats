package com.github.mathlazaro.view;

import com.github.mathlazaro.model.flower.DistanceMethod;
import com.github.mathlazaro.model.flower.ResponseDistanceCalcDTO;
import distribuidos.api.model.Dimension;
import distribuidos.api.model.FlowerFeature;

import static com.github.mathlazaro.MainClient.IN;

/**
 * Classe para interação com o usuário via terminal, responsável por ler as
 * entradas do usuário e exibir os resultados relacionados às operações de
 * cálculo de distância entre flores.
 */
public class FlowerView implements View {

    private static FlowerView INSTANCE;

    private FlowerView() {

    }

    public static FlowerView getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FlowerView();
        }
        return INSTANCE;
    }

    public DistanceMethod readDistanceMethod() {
        try {
            System.out.println("Escolha o método de distância (1 ou 2):");
            System.out.println("1. Distância Euclidiana");
            System.out.println("2. Distância City Block");
            System.out.print("Opção: ");
            int option = IN.nextInt();
            IN.nextLine();

            System.out.printf("%n");

            return switch (option) {
                case 1 -> DistanceMethod.EUCLIDEAN;
                case 2 -> DistanceMethod.CITY_BLOCK;
                default -> throw new IllegalArgumentException("Opção inválida");
            };
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + " Tente novamente.");
            return readDistanceMethod();
        } catch (Exception e) {
            System.err.println("Erro ao ler a opção de método de distância. Tente novamente.");
            return readDistanceMethod();
        }

    }

    public FlowerFeature readFlowerFeature(String description) {
        try {
            System.out.printf("%n");
            System.out.printf("Digite as dimensões da flor '%s':%n", description);

            Dimension petalDimension = readDimension("pétala");
            Dimension sepalDimension = readDimension("sépala");

            System.out.printf("%n");
            return new FlowerFeature(description, petalDimension, sepalDimension);
        } catch (Exception e) {
            System.err.println("Erro ao ler as dimensões da flor. Tente novamente.");
            return readFlowerFeature(description);
        }
    }

    private Dimension readDimension(String name) {
        System.out.printf("Dimensão da %s:%n", name);
        double width = readDouble(" . Largura: ");
        double height = readDouble(" . Altura: ");
        return new Dimension(height, width);
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = IN.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                return Double.parseDouble(input.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.err.println("Valor inválido. Digite um número.");
            }
        }
    }

    public void displayDistance(ResponseDistanceCalcDTO response) {
        System.out.printf("Distância entre %s e %s usando %s: %.4f%n",
                response.featureA().description(),
                response.featureB().description(),
                response.method().toString(),
                response.distance()
        );
    }

}
