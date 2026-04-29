package com.github.mathlazaro.model.flower;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import distribuidos.api.model.FlowerFeature;
import lombok.extern.log4j.Log4j2;

@Log4j2
public record RequestDistanceCalcDTO(
        FlowerFeature featureA,
        FlowerFeature featureB
) {

    public byte[] serialize(ObjectMapper mapper) {
        try {
            return mapper.writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar objeto", e);
            throw new RuntimeException("Erro ao serializar objeto");
        }

    }

}
