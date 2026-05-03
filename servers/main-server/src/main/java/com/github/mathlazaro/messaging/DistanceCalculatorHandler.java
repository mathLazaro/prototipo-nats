package com.github.mathlazaro.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.model.flower.DistanceMethod;
import com.github.mathlazaro.model.flower.RequestDistanceCalcDTO;
import com.github.mathlazaro.model.flower.ResponseDistanceCalcDTO;
import com.github.mathlazaro.service.DistanceCalculatorServiceImpl;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * Classe responsável por lidar com requisições de cálculo de distância entre flores
 * <p>
 * Suporta dois métodos de cálculo: Euclidiana e City Block (Manhattan)
 * Retorna a distância calculada e os pontos utilizados no cálculo
 */
@AllArgsConstructor
@Log4j2
public class DistanceCalculatorHandler {

    private final DistanceCalculatorServiceImpl calculatorService;

    private final ObjectMapper objectMapper;

    /**
     * Calcula a distância Euclidiana entre dois pontos de características de flores
     * <p>
     * Fórmula: sqrt((x1-x2)² + (y1-y2)²)
     * Representa a distância geométrica direta entre os pontos
     *
     * @param message mensagem NATS contendo RequestDistanceCalcDTO com os dois pontos
     * @return EventDTO<?> com ResponseDistanceCalcDTO contendo o resultado do cálculo ou erro
     */
    public EventDTO<?> handleEuclideanCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            log.info("Cálculo Euclidiano solicitado: {} até {}", request.featureA(), request.featureB());

            double result = calculatorService.calculateDistanceByEclidean(request.featureA(), request.featureB());
            ResponseDistanceCalcDTO response = new ResponseDistanceCalcDTO(
                    DistanceMethod.EUCLIDEAN,
                    request.featureA(),
                    request.featureB(),
                    result
            );

            log.info("Distância Euclidiana calculada: {}", result);
            return EventDTO.success(response);
        } catch (IOException e) {
            log.error("Erro ao desserializar requisição de cálculo Euclidiano", e);
            return EventDTO.error("Erro ao desserializar requisição");
        }
    }

    /**
     * Calcula a distância City Block (Manhattan) entre dois pontos de características de flores
     * <p>
     * Fórmula: |x1-x2| + |y1-y2|
     * Representa a distância percorrida em um grid (como em um mapa de cidade)
     *
     * @param message mensagem NATS contendo RequestDistanceCalcDTO com os dois pontos
     * @return EventDTO<?> com ResponseDistanceCalcDTO contendo o resultado do cálculo ou erro
     */
    public EventDTO<?> handleCityBlockCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            log.info("Cálculo City Block solicitado: {} até {}", request.featureA(), request.featureB());

            double result = calculatorService.calculateDistanceByCityBlock(request.featureA(), request.featureB());
            ResponseDistanceCalcDTO response = new ResponseDistanceCalcDTO(
                    DistanceMethod.CITY_BLOCK,
                    request.featureA(),
                    request.featureB(),
                    result
            );

            log.info("Distância City Block calculada: {}", result);
            return EventDTO.success(response);
        } catch (IOException e) {
            log.error("Erro ao desserializar requisição de cálculo City Block", e);
            return EventDTO.error("Erro ao desserializar requisição");
        }
    }

    /**
     * Desserializa a mensagem NATS de bytes para RequestDistanceCalcDTO
     *
     * @param message mensagem NATS contendo os dados de requisição
     * @return RequestDistanceCalcDTO contendo os dois pontos para cálculo
     * @throws IOException se falhar na desserialização JSON
     */
    private RequestDistanceCalcDTO deserialize(Message message) throws IOException {
        byte[] data = message.getData();

        return objectMapper.readValue(data, RequestDistanceCalcDTO.class);
    }

}
