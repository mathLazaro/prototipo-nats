package com.github.mathlazaro.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.model.RequestFileDTO;
import com.github.mathlazaro.service.FileService;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@AllArgsConstructor
@Log4j2
public class FileHandler {

    private final FileService fileService;

    private final ObjectMapper mapper;

    public MessageHandler handleSaveFile() {
        return msg -> {
            byte[] data = msg.getData();
            try {
                RequestFileDTO request = mapper.readValue(data, RequestFileDTO.class);
                fileService.saveFile(request.fileName(), request.data());
                log.info("Arquivo {} salvo com sucesso", request.fileName());
            } catch (IOException e) {
                log.error("Erro ao desserializar evento 'file.save'", e);
            }
        };
    }

    public EventDTO<?> handleReadFile(Message msg) {
        try {
            byte[] data = msg.getData();
            RequestFileDTO request = mapper.readValue(data, RequestFileDTO.class);
            byte[] file = fileService.readFile(request.fileName());
            log.info("Arquivo {} lido com sucesso", request.fileName());
            return EventDTO.success(file);
        } catch (IOException e) {
            log.error("Erro ao desserializar evento 'file.read'", e);
            return EventDTO.error("Erro ao processar a solicitação de leitura do arquivo: " + e.getMessage());
        }
    }

    public MessageHandler handleAppendFile() {
        return msg -> {
            byte[] data = msg.getData();
            try {
                RequestFileDTO request = mapper.readValue(data, RequestFileDTO.class);
                fileService.appendFile(request.fileName(), request.data());
                log.info("Dados adicionados ao arquivo {} com sucesso", request.fileName());
            } catch (IOException e) {
                log.error("Erro ao desserializar evento 'file.append'", e);
            }
        };
    }

    public MessageHandler handleDeleteFile() {
        return msg -> {
            byte[] data = msg.getData();
            try {
                RequestFileDTO request = mapper.readValue(data, RequestFileDTO.class);
                fileService.deleteFile(request.fileName());
                log.info("Arquivo {} deletado com sucesso", request.fileName());
            } catch (IOException e) {
                log.error("Erro ao desserializar evento 'file.delete'", e);
            }
        };
    }

}
