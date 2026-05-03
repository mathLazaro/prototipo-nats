package com.github.mathlazaro;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.configuration.NatsConnectionManager;
import com.github.mathlazaro.messaging.DistanceCalculatorHandler;
import com.github.mathlazaro.messaging.MessageRequestHandler;
import com.github.mathlazaro.service.DistanceCalculatorServiceImpl;
import com.github.mathlazaro.service.JokesApiClient;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class MainServer {

    private final static String NATS_URL = System.getenv().getOrDefault("NATS_URL", "nats://localhost:4222");

    private final static ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String[] args) throws Exception {
        log.info("main-producer starting");

        // Inicia a conexão com o NATS e configura os handlers para as mensagens
        DistanceCalculatorHandler calculatorHandler = new DistanceCalculatorHandler(new DistanceCalculatorServiceImpl(), objectMapper);
        MessageRequestHandler messageRequestHandler = new MessageRequestHandler(new JokesApiClient(objectMapper));

        NatsConnectionManager connManager = new NatsConnectionManager(NATS_URL, "main-producer", objectMapper);

        // Configura os handlers para as mensagens de distância e mensagens gerais
        connManager.publishReply("distances.euclidean", calculatorHandler::handleEuclideanCalculation);
        connManager.publishReply("distances.cityblock", calculatorHandler::handleCityBlockCalculation);

        connManager.publishReply("jokes.*", messageRequestHandler::handleJokeRequest);
        connManager.publishReply("greetings", messageRequestHandler::handleGreetingRequest);

    }

}
