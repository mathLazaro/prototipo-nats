package com.github.mathlazaro.messaging;

import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.service.JokesApiClient;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;

/**
 * Classe responsável por lidar com requisições de piadas e mensagens de saudação
 */
@AllArgsConstructor
@Log4j2
public class MessageRequestHandler {

    private final JokesApiClient jokesApiClient;

    private final List<String> greetings = List.of("oi", "ola", "eai", "como vai", "bom", "boa", "tudo");

    /**
     * Processa requisições de piadas via API externa
     * <p>
     * Extrai o tipo de piada do subject NATS (ex: "joke.fetch.programming" -> "programming")
     * e busca uma piada desse tipo através do JokesApiClient
     *
     * @param message mensagem NATS contendo a requisição
     * @return EventDTO<?> com a piada formatada como sucesso ou erro se falhar
     */
    public EventDTO<?> handleJokeRequest(Message message) {

        String subject = Optional.ofNullable(message.getSubject()).orElse("");
        String type = List.of(subject.split("[.]")).getLast();
        log.info("Requisição recebida de piada -> tipo {}", type);

        String joke = jokesApiClient.fetchJoke(type)
                .map(dto -> dto.setup() + "... " + dto.punchline())
                .orElse("");
        return EventDTO.success(joke);
    }

    /**
     * Processa requisições de saudação (greeting)
     * <p>
     * Analisa a mensagem recebida e identifica se contém palavras de saudação
     * Retorna uma resposta amigável ou mensagem de incompreensão
     *
     * @param message mensagem NATS contendo o texto de saudação
     * @return EventDTO<?> com resposta de saudação
     */
    public EventDTO<?> handleGreetingRequest(Message message) {
        String request = deserialize(message);
        log.info("Requisição de saudação recebida: {}", request);

        if (greetings.stream().anyMatch(greeting -> request.toLowerCase().contains(greeting) || request.equalsIgnoreCase(greeting))) {
            log.info("Saudação identificada");
            return EventDTO.success("Olá tudo bem? Mensagem recebida!");
        }

        log.info("Mensagem não reconhecida como saudação");
        return EventDTO.success("Mensagem recebida, porém ainda estou aprendendo :/");

    }

    /**
     * Desserializa a mensagem NATS de bytes para String
     *
     * @param message mensagem NATS
     * @return conteúdo da mensagem como String
     */
    private String deserialize(Message message) {
        byte[] body = message.getData();
        return new String(body);
    }

}
