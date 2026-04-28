package com.github.mathlazaro;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.configuration.NatsConnectionManager;
import com.github.mathlazaro.messaging.FileHandler;
import com.github.mathlazaro.service.FileService;
import io.nats.client.Connection;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainFileServer {

    private final static String NATS_URL = System.getenv().getOrDefault("NATS_URL", "nats://localhost:4222");

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static void main() {

        log.info("file-server starting");

        FileHandler fileHandler = new FileHandler(new FileService(), objectMapper);

        NatsConnectionManager connManager = new NatsConnectionManager(NATS_URL, "file-server", objectMapper);

        Connection nc = connManager.getNc();

        nc.createDispatcher(fileHandler.handleSaveFile()).subscribe("file.save", "file-queue");
        nc.createDispatcher(fileHandler.handleReadFile()).subscribe("file.read", "file-queue");
        nc.createDispatcher(fileHandler.handleAppendFile()).subscribe("file.append", "file-queue");
        nc.createDispatcher(fileHandler.handleDeleteFile()).subscribe("file.delete", "file-queue");

    }
}
