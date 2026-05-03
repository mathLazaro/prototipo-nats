package com.github.mathlazaro.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.model.joke.JokeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Cliente HTTP para integração com API de piadas (official-joke-api)
 * <p>
 * Responsável por buscar piadas da API externa e deserializá-las em objetos JokeResponseDTO
 * Realiza requisições HTTPS com timeout para evitar bloqueios indefinidos
 * Suporta tipos específicos de piadas: programming, general, knock-knock, dad
 *
 * @see <a href="https://official-joke-api.appspot.com/">Official Joke API</a>
 */
@Log4j2
@AllArgsConstructor
public class JokesApiClient {

    private static final String API_URL = "https://official-joke-api.appspot.com/jokes/%s";

    private static final List<String> VALID_TYPES = List.of("programming", "general", "knock-knock", "dad");

    private final ObjectMapper mapper;

    /**
     * Busca uma piada da API externa com base no tipo fornecido
     * <p>
     * Realiza uma requisição HTTPS GET para a API de piadas
     * Com timeouts de conexão e leitura para evitar bloqueios
     *
     * @param type tipo de piada desejada (programming, general, knock-knock, dad, ou qualquer outra para aleatória)
     * @return Optional contendo JokeResponseDTO se sucesso, Optional vazio se falhar
     */
    public Optional<JokeResponseDTO> fetchJoke(String type) {
        URL url = defineURL(type);

        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);  // 5 segundos para ler resposta
            connection.setConnectTimeout(5000); // 5 segundos para conectar

            log.info("Buscando piada da API: {}", url);
            return Optional.ofNullable(deserializeResponse(connection.getInputStream(), type));
        } catch (Exception e) {
            log.error("Erro ao buscar piada da API: {}", e.getMessage(), e);
            return Optional.empty();
        }

    }

    /**
     * Desserializa a resposta HTTPS em um objeto JokeResponseDTO
     * <p>
     * Para tipos válidos, a API retorna um array JSON
     * Para tipos inválidos, retorna um objeto único
     *
     * @param input InputStream da resposta HTTP
     * @param type  tipo de piada para processamento diferenciado
     * @return JokeResponseDTO ou null se falhar na desserialização
     * @throws RuntimeException se ocorrer erro de I/O durante desserialização
     */
    private JokeResponseDTO deserializeResponse(InputStream input, String type) {
        try {
            if (input == null) {
                log.warn("Input stream nulo ao desserializar resposta de piada");
                return null;
            }

            JokeResponseDTO dto;
            if (VALID_TYPES.contains(type)) {
                // API retorna array para tipos válidos
                JokeResponseDTO[] arr = mapper.readValue(input, JokeResponseDTO[].class);
                dto = (arr != null && arr.length > 0) ? arr[0] : null;
                log.debug("Piada deserializada de array (tipo {})", type);
            } else {
                // API retorna objeto único para tipos não reconhecidos
                dto = mapper.readValue(input, JokeResponseDTO.class);
                log.debug("Piada deserializada de objeto único (tipo aleatório)");
            }

            return dto;
        } catch (IOException e) {
            log.error("Erro ao desserializar resposta da API de piadas", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Constrói a URL final para a requisição à API
     * <p>
     * Tipos válidos (programming, general, knock-knock, dad) usam endpoint específico
     * Outros tipos recebem endpoint "random" para piada aleatória
     *
     * @param type tipo de piada
     * @return URL construída
     * @throws RuntimeException se falhar ao criar URL
     */
    private static URL defineURL(String type) {
        URI uri;
        if (VALID_TYPES.contains(type)) {
            // Tipo válido: fetch específico
            uri = URI.create(String.format(API_URL, type + "/random"));
            log.debug("URL definida para tipo específico {}: {}", type, uri);
        } else {
            // Tipo desconhecido: piada aleatória
            uri = URI.create(String.format(API_URL, "random"));
            log.debug("URL definida para piada aleatória (tipo desconhecido: {})", type);
        }

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            log.error("Erro ao definir URL da API de piadas", e);
            throw new RuntimeException("Erro ao definir URL", e);
        }

    }

}
