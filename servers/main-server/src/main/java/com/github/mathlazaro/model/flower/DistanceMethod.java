package com.github.mathlazaro.model.flower;


import lombok.Getter;

public enum DistanceMethod {
    EUCLIDEAN("distances.euclidean"),
    CITY_BLOCK("distances.cityblock");

    @Getter
    private final String subject;

    DistanceMethod(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
