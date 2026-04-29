package com.github.mathlazaro.model.flower;

import distribuidos.api.model.FlowerFeature;

public record RequestDistanceCalcDTO(
        FlowerFeature featureA,
        FlowerFeature featureB
) {
}