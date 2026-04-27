package com.github.mainproducer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mainproducer.model.JokeResponseDTO;
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
import java.util.stream.Stream;

@Log4j2
@AllArgsConstructor
public class JokesApiClient {

    private static final String API_URL = "https://official-joke-api.appspot.com/jokes/%s";

    private static final List<String> VALID_TYPES = List.of("programming", "general", "knock-knock", "dad");

    private final ObjectMapper mapper;

    public Optional<JokeResponseDTO> fetchJoke(String type) {
        URL url = defineURL(type);

        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                return Optional.ofNullable(deserializeResponse(connection.getInputStream(), type));
            } else {
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

    }

    private JokeResponseDTO deserializeResponse(InputStream input, String type) {
        try {
            JokeResponseDTO dto;
            if (VALID_TYPES.contains(type)) {
                dto = Stream.of(mapper.readValue(input, JokeResponseDTO[].class)).findFirst().orElse(null);
            } else {
                dto = mapper.readValue(input, JokeResponseDTO.class);
            }

            return dto;
        } catch (IOException e) {
            log.error("Erro ao deserializar joke response", e);
            throw new RuntimeException(e);
        }
    }


    private static URL defineURL(String type) {
        URI uri;
        if (VALID_TYPES.contains(type)) {
            uri = URI.create(String.format(API_URL, type + "/random"));
        } else {
            uri = URI.create(String.format(API_URL, "random"));
        }

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            log.error("Erro ao definir URL", e);
            throw new RuntimeException("Erro ao definir URL");
        }

    }

}
