package com.github.mainproducer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JokeResponseDTO(String type, String setup, String punchline) {
}
