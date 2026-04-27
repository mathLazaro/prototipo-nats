package com.github.mainproducer.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mainproducer.model.ReplyDTO;
import com.github.mainproducer.model.RequestDistanceCalcDTO;
import com.github.mainproducer.service.DistanceCalculatorServiceImpl;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@AllArgsConstructor
@Log4j2
public class DistanceCalculatorHandler {

    private final DistanceCalculatorServiceImpl calculatorService;

    private final ObjectMapper objectMapper;

    public ReplyDTO<?> handleEuclideanCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            double result = calculatorService.calculateDistanceByEclidean(request.features1(), request.features2());
            return ReplyDTO.success(result);
        } catch (IOException e) {
            return ReplyDTO.error("Erro ao desserializar requisição");
        }
    }

    public ReplyDTO<?> handleCityBlockCalculation(Message message) {
        try {
            RequestDistanceCalcDTO request = deserialize(message);
            double result = calculatorService.calculateDistanceByCityBlock(request.features1(), request.features2());
            return ReplyDTO.success(result);
        } catch (IOException e) {
            return ReplyDTO.error("Erro ao desserializar requisição");
        }
    }

    private RequestDistanceCalcDTO deserialize(Message message) throws IOException {
        byte[] data = message.getData();

        return objectMapper.readValue(data, RequestDistanceCalcDTO.class);
    }

}
