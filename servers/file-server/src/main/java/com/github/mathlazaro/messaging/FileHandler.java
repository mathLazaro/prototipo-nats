package com.github.mathlazaro.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.model.RequestFileDTO;
import com.github.mathlazaro.service.FileService;
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
                fileService.saveFile(request.fileName(), request.data().readAllBytes());
            } catch (IOException e) {
                log.error("Erro ao deserializar evento 'file.save'", e);
                throw new RuntimeException(e);
            }
        };
    }

    public MessageHandler handleReadFile() {
        return msg -> {
            byte[] data = msg.getData();
            try {
                RequestFileDTO request = mapper.readValue(data, RequestFileDTO.class);
                fileService.readFile(request.fileName());
            } catch (IOException e) {
                log.error("Erro ao deserializar evento 'file.read'", e);
                throw new RuntimeException(e);
            }
        };
    }

    public MessageHandler handleAppendFile() {
        return msg -> {
            byte[] data = msg.getData();
            try {
                RequestFileDTO request = mapper.readValue(data, RequestFileDTO.class);
                fileService.appendFile(request.fileName(), request.data().readAllBytes());
            } catch (IOException e) {
                log.error("Erro ao deserializar evento 'file.append'", e);
                throw new RuntimeException(e);
            }
        };
    }

    public MessageHandler handleDeleteFile() {
        return msg -> {
            byte[] data = msg.getData();
            try {
                RequestFileDTO request = mapper.readValue(data, RequestFileDTO.class);
                fileService.deleteFile(request.fileName());
            } catch (IOException e) {
                log.error("Erro ao deserializar evento 'file.delete'", e);
                throw new RuntimeException(e);
            }
        };
    }

}
