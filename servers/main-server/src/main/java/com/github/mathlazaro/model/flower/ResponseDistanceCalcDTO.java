package com.github.mathlazaro.model.flower;

import distribuidos.api.model.FlowerFeature;

public record ResponseDistanceCalcDTO(
        DistanceMethod method,
        FlowerFeature featureA,
        FlowerFeature featureB,
        Double distance
) {
}