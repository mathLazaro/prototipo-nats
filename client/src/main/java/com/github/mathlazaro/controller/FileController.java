package com.github.mathlazaro.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mathlazaro.MainClient;
import com.github.mathlazaro.domain.EventDTO;
import com.github.mathlazaro.model.file.FileOperation;
import com.github.mathlazaro.model.file.RequestFileDTO;
import com.github.mathlazaro.view.FileView;
import com.github.mathlazaro.view.View;
import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.Duration;

@Log4j2
@AllArgsConstructor
public class FileController {

    private final FileView view = FileView.getInstance();

    private final Connection nc;

    private final ObjectMapper mapper;

    public void run() {
        do {
            FileOperation operation = view.readOperation();

            switch (operation) {
                case SAVE -> handleSaveOp();
                case READ -> handleReadOp();
                case APPEND -> handleAppendOp();
                case DELETE -> handleDeleteOp();
            }

        } while (View.readKeepRunning(MainClient.IN));
    }

    private void handleSaveOp() {
        try {
            String fileName = view.readFileName();
            byte[] fileData = view.readFileData();

            RequestFileDTO request = new RequestFileDTO(fileName, fileData);
            byte[] messageData = mapper.writeValueAsBytes(request);
            nc.publish(FileOperation.SAVE.getSubject(), messageData);
            log.info("Arquivo '{}' publicado para salvamento", fileName);
            view.displaySaveSuccess(fileName);

        } catch (IOException e) {
            log.error("Erro ao publicar operação de salvamento: {}", e.getMessage(), e);
            view.displayError("Erro ao salvar arquivo");
        }
    }

    private void handleReadOp() {
        try {
            String fileName = view.readFileName();

            RequestFileDTO request = new RequestFileDTO(fileName, null);
            byte[] messageData = mapper.writeValueAsBytes(request);
            Message msg = nc.request(FileOperation.READ.getSubject(), messageData, Duration.ofSeconds(5));
            if (msg == null) {
                throw new IOException("Nenhuma resposta recebida para a solicitação de leitura do arquivo");
            }
            EventDTO<byte[]> response = mapper.readValue(msg.getData(), new TypeReference<>() {
            });
            view.displayReadSuccess(response.payload());
        } catch (IOException e) {
            log.error("Erro ao publicar operação de leitura: {}", e.getMessage(), e);
            view.displayError("Erro ao ler arquivo");
        } catch (InterruptedException e) {
            view.displayError("Operação de leitura interrompida");
            Thread.currentThread().interrupt();
        }
    }

    private void handleAppendOp() {
        try {
            String fileName = view.readFileName();
            byte[] fileData = view.readFileData();

            RequestFileDTO request = new RequestFileDTO(fileName, fileData);
            byte[] messageData = mapper.writeValueAsBytes(request);
            nc.publish(FileOperation.APPEND.getSubject(), messageData);
            log.info("Dados publicados para adição ao arquivo '{}'", fileName);
            view.displayAppendSuccess(fileName);

        } catch (IOException e) {
            log.error("Erro ao publicar operação de adição: {}", e.getMessage(), e);
            view.displayError("Erro ao adicionar dados ao arquivo");
        }
    }

    private void handleDeleteOp() {
        try {
            String fileName = view.readFileName();

            RequestFileDTO request = new RequestFileDTO(fileName, null);
            byte[] messageData = mapper.writeValueAsBytes(request);
            nc.publish(FileOperation.DELETE.getSubject(), messageData);
            log.info("Solicitação de deleção do arquivo '{}' publicada", fileName);
            view.displayDeleteSuccess(fileName);

        } catch (IOException e) {
            log.error("Erro ao publicar operação de deleção: {}", e.getMessage(), e);
            view.displayError("Erro ao deletar arquivo");
        }
    }

}
