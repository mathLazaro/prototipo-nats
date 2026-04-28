package com.github.mathlazaro.messaging;

import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.service.JokesApiClient;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Log4j2
public class MessageRequestHandler {

    private final JokesApiClient jokesApiClient;

    private final List<String> greetings = List.of("oi", "ola", "eai", "como vai", "bom", "boa", "tudo");

    public EventDTO<?> handleJokeRequest(Message message) {
        String request = deserialize(message);
        log.info("Requisição recebida: {}", request);

        String subject = Optional.ofNullable(message.getSubject()).orElse("");
        String type = List.of(subject.split("[.]")).getLast();
        String joke = jokesApiClient.fetchJoke(type).map(dto -> dto.setup() + "... " + dto.punchline()).orElse("");
        return EventDTO.success(joke);
    }

    public EventDTO<?> handleGreetingRequest(Message message) {
        String request = deserialize(message);
        log.info("Requisição recebida: {}", request);

        if (greetings.stream().anyMatch(greeting -> request.toLowerCase().contains(greeting) || request.equalsIgnoreCase(greeting))) {
            return EventDTO.success("Olá tudo bem? Mensagem recebida!");
        }

        return EventDTO.success("Mensagem recebida, porém ainda estou aprendendo :/");

    }

    private String deserialize(Message message) {
        byte[] body = message.getData();
        return new String(body);
    }

}
