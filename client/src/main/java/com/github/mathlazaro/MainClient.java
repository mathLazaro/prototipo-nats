package com.github.mathlazaro;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.configuration.NatsConnectionManager;
import com.github.mathlazaro.controller.FileController;
import com.github.mathlazaro.controller.FlowerController;
import com.github.mathlazaro.controller.MessageController;
import com.github.mathlazaro.model.main.MainOperation;
import com.github.mathlazaro.view.MainView;
import com.github.mathlazaro.view.View;
import lombok.extern.log4j.Log4j2;

import java.util.Scanner;

@Log4j2
public class MainClient {

    private final static String NATS_URL = System.getenv().getOrDefault("NATS_URL", "nats://localhost:4222");

    private final static ObjectMapper mapper = new ObjectMapper();

    public final static Scanner IN = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        log.info("client iniciando");


        // Cria a conexão via manager
        NatsConnectionManager connManager = new NatsConnectionManager(NATS_URL, "client", mapper);

        // Instanciação dos controllers
        FlowerController flowerController = new FlowerController(connManager.getNc(), mapper);
        MessageController messageController = new MessageController(connManager.getNc(), mapper);
        FileController fileController = new FileController(connManager.getNc(), mapper);

        MainView mainView = new MainView();

        // Execução do loop de interação com o usuário
        do {
            try {
                MainOperation operation = mainView.readMainOperation();

                switch (operation) {
                    case FLOWER_CALCULATION -> flowerController.run();
                    case MESSAGE -> messageController.run();
                    case FILE -> fileController.run();
                }
            } catch (Exception e) {
                log.error(e);
            }
        } while (View.readKeepRunning(IN));

        // Fecha a conexão ao finalizar o loop
        connManager.closeConnection();

    }
}
