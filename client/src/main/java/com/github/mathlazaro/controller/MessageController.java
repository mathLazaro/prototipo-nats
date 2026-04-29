package com.github.mathlazaro.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.MainClient;
import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.model.message.JokeType;
import com.github.mathlazaro.model.message.MessageOperation;
import com.github.mathlazaro.view.MessageView;
import com.github.mathlazaro.view.View;
import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.Duration;

@Log4j2
@AllArgsConstructor
public class MessageController {

    private final MessageView view = MessageView.getInstance();

    private final Connection nc;

    private final ObjectMapper mapper;

    public void run() {
        do {
            MessageOperation operation = view.readOperation();

            switch (operation) {
                case JOKE -> handleJokeOp();
                case GREETINGS -> handleGreetingOp();
            }

        } while (View.readKeepRunning(MainClient.IN));
    }

    private void handleJokeOp() {
        JokeType jokeType = view.readJokeType();

        String subject = MessageOperation.JOKE.getSubject() + "." + jokeType.getSubject();

        try {
            Message msg = nc.request(subject, null, Duration.ofSeconds(3));
            if (msg == null) {
                System.err.println("Resposta nula recebida para requisição de piada. Verifique se o servidor está respondendo corretamente.");
                return;
            }
            EventDTO<String> response = mapper.readValue(msg.getData(), new TypeReference<>() {
            });
            String joke = response.payload();
            view.displayJokeResponse(joke);
        } catch (IOException e) {
            log.error("Erro ao processar resposta: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGreetingOp() {
        String greeting = view.readMessage();
        String subject = MessageOperation.GREETINGS.getSubject();

        try {
            Message msg = nc.request(subject, greeting.getBytes(), Duration.ofSeconds(3));
            if (msg == null) {
                System.err.println("Resposta nula recebida para a mensagem de saudação. Verifique se o servidor está respondendo corretamente.");
                return;
            }
            EventDTO<String> response = mapper.readValue(msg.getData(), new TypeReference<>() {
            });
            String message = response.payload();
            view.displayGreetingResponse(message);
        } catch (IOException e) {
            log.error("Erro ao processar resposta: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
