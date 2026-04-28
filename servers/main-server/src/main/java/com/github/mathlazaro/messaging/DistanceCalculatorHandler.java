package com.github.mathlazaro.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.model.RequestDistanceCalcDTO;
import com.github.mathlazaro.service.DistanceCalculatorServiceImpl;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@AllArgsConstructor
@Log4j2
public class DistanceCalculatorHandler {

    private final DistanceCalculatorServiceImpl calculatorService;

    private final ObjectMapper objectMapper;

    public EventDTO<?> handleEuclideanCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            double result = calculatorService.calculateDistanceByEclidean(request.features1(), request.features2());
            return EventDTO.success(result);
        } catch (IOException e) {
            return EventDTO.error("Erro ao desserializar requisição");
        }
    }

    public EventDTO<?> handleCityBlockCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            double result = calculatorService.calculateDistanceByCityBlock(request.features1(), request.features2());
            return EventDTO.success(result);
        } catch (IOException e) {
            return EventDTO.error("Erro ao desserializar requisição");
        }
    }

    private RequestDistanceCalcDTO deserialize(Message message) throws IOException {
        byte[] data = message.getData();

        return objectMapper.readValue(data, RequestDistanceCalcDTO.class);
    }

}
