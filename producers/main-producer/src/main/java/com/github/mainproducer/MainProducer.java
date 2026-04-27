package com.github.mainproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mainproducer.configuration.NatsConnectionManager;
import com.github.mainproducer.messaging.DistanceCalculatorHandler;
import com.github.mainproducer.messaging.MessageRequestHandler;
import com.github.mainproducer.service.DistanceCalculatorServiceImpl;
import com.github.mainproducer.service.JokesApiClient;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class MainProducer {

    private final static String NATS_URL = System.getenv().getOrDefault("NATS_URL", "nats://localhost:4222");

    private final static ObjectMapper objectMapper = new ObjectMapper();


    static void main(String[] args) {
        log.info("main-producer starting");

        DistanceCalculatorHandler calculatorHandler = new DistanceCalculatorHandler(new DistanceCalculatorServiceImpl(), objectMapper);
        MessageRequestHandler messageRequestHandler = new MessageRequestHandler(new JokesApiClient(objectMapper));

        NatsConnectionManager connManager = new NatsConnectionManager(NATS_URL, "main-producer", objectMapper);

        connManager.publishReply("distances.euclidean", calculatorHandler::handleEuclideanCalculation);
        connManager.publishReply("distances.cityblock", calculatorHandler::handleCityBlockCalculation);

        connManager.publishReply("jokes.*", messageRequestHandler::handleJokeRequest);
        connManager.publishReply("greetings", messageRequestHandler::handleGreetingRequest);

    }

}
