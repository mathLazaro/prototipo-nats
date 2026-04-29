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

@AllArgsConstructor
@Log4j2
public class DistanceCalculatorHandler {

    private final DistanceCalculatorServiceImpl calculatorService;

    private final ObjectMapper objectMapper;

    public EventDTO<?> handleEuclideanCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            double result = calculatorService.calculateDistanceByEclidean(request.featureA(), request.featureB());
            ResponseDistanceCalcDTO response = new ResponseDistanceCalcDTO(DistanceMethod.EUCLIDEAN, request.featureA(), request.featureB(), result);
            return EventDTO.success(response);
        } catch (IOException e) {
            return EventDTO.error("Erro ao desserializar requisição");
        }
    }

    public EventDTO<?> handleCityBlockCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            double result = calculatorService.calculateDistanceByCityBlock(request.featureA(), request.featureB());
            ResponseDistanceCalcDTO response = new ResponseDistanceCalcDTO(DistanceMethod.CITY_BLOCK, request.featureA(), request.featureB(), result);
            return EventDTO.success(response);
        } catch (IOException e) {
            return EventDTO.error("Erro ao desserializar requisição");
        }
    }

    private RequestDistanceCalcDTO deserialize(Message message) throws IOException {
        byte[] data = message.getData();

        return objectMapper.readValue(data, RequestDistanceCalcDTO.class);
    }

}
