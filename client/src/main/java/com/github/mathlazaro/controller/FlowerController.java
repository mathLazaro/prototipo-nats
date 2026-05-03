package com.github.mathlazaro.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.MainClient;
import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.model.flower.DistanceMethod;
import com.github.mathlazaro.model.flower.RequestDistanceCalcDTO;
import com.github.mathlazaro.model.flower.ResponseDistanceCalcDTO;
import com.github.mathlazaro.view.FlowerView;
import com.github.mathlazaro.view.View;
import distribuidos.api.model.FlowerFeature;
import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.Duration;


/**
 * Controller responsável por gerenciar a interação do usuário com as funcionalidades de cálculo de distância entre flores
 */
@Log4j2
@AllArgsConstructor
public class FlowerController {

    private final FlowerView view = FlowerView.getInstance();

    private final Connection nc;

    private final ObjectMapper mapper;

    /**
     * Executa o loop de interação com o usuário para ler as características das flores, a tipo de cálculo e solicitar a distância calculada
     */
    public void run() {
        do {
            FlowerFeature featureA = view.readFlowerFeature("A");
            FlowerFeature featureB = view.readFlowerFeature("B");

            DistanceMethod method = view.readDistanceMethod();

            requestCalculation(featureA, featureB, method);
        } while (View.readKeepRunning(MainClient.IN));
    }

    private void requestCalculation(FlowerFeature featureA, FlowerFeature featureB, DistanceMethod method) {
        try {
            Message msg = nc.request(method.getSubject(), new RequestDistanceCalcDTO(featureA, featureB).serialize(mapper), Duration.ofSeconds(3));
            EventDTO<ResponseDistanceCalcDTO> response = mapper.readValue(msg.getData(), new TypeReference<>() {
            });
            view.displayDistance(response.payload());
        } catch (IOException e) {
            log.error("Erro ao processar resposta: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
