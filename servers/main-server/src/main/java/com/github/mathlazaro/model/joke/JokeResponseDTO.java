package com.github.mathlazaro.model.joke;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JokeResponseDTO(String type, String setup, String punchline) {
}
