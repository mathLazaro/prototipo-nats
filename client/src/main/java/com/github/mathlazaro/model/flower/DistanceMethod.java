package com.github.mathlazaro.model.flower;


import lombok.Getter;

public enum DistanceMethod {
    EUCLIDEAN("distances.euclidean"),
    CITY_BLOCK("distances.cityblock");

    @Getter
    private final String subject;

    private DistanceMethod(String subject) {
        this.subject = subject;
    }

    public static DistanceMethod fromString(String method) {
        return switch (method.toLowerCase()) {
            case "euclidean" -> EUCLIDEAN;
            case "cityblock" -> CITY_BLOCK;
            default -> throw new IllegalArgumentException("Método de distância desconhecido: " + method);
        };
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
