package com.github.mathlazaro.service;

import distribuidos.api.model.FlowerFeature;
import distribuidos.api.service.DistanceCalculatorService;

/**
 * Implementação do serviço de cálculo de proximidade entre flores.
 */
public class DistanceCalculatorServiceImpl implements DistanceCalculatorService {

    /**
     * Calcula a distância entre duas flores usando a métrica de distância
     * euclidiana.
     *
     * @param features1 As características da primeira flor.
     * @param features2 As características da segunda flor.
     * @return A distância euclidiana entre as duas flores.
     */
    @Override
    public double calculateDistanceByEclidean(FlowerFeature features1, FlowerFeature features2) {

        double sum = 0.0;
        sum += Math.pow(features1.sepalDimension().height() - features2.sepalDimension().height(), 2);
        sum += Math.pow(features1.sepalDimension().width() - features2.sepalDimension().width(), 2);

        sum += Math.pow(features1.petalDimension().height() - features2.petalDimension().height(), 2);
        sum += Math.pow(features1.petalDimension().width() - features2.petalDimension().width(), 2);

        return Math.sqrt(sum);
    }

    /**
     * Calcula a distância entre duas flores usando a métrica de distância por
     * blocos (City Block).
     *
     * @param features1 As características da primeira flor.
     * @param features2 As características da segunda flor.
     * @return A distância por blocos entre as duas flores.
     */
    @Override
    public double calculateDistanceByCityBlock(FlowerFeature features1, FlowerFeature features2) {

        double sum = 0.0;
        sum += Math.abs(features1.sepalDimension().height() - features2.sepalDimension().height());
        sum += Math.abs(features1.sepalDimension().width() - features2.sepalDimension().width());
        sum += Math.abs(features1.petalDimension().height() - features2.petalDimension().height());
        sum += Math.abs(features1.petalDimension().width() - features2.petalDimension().width());

        return sum;
    }

}
