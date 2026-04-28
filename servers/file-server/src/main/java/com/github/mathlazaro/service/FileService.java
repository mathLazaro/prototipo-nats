package com.github.mathlazaro.service;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Log4j2
public class FileService {

    private static final String TEMP_DIR = "TEMP";
    private static final Path TEMP_PATH = Paths.get(TEMP_DIR);

    static {
        try {
            Files.createDirectories(TEMP_PATH);
            log.info("Diretório TEMP criado ou já existe em: {}", TEMP_PATH.toAbsolutePath());
        } catch (IOException e) {
            log.error("Falha ao criar diretório TEMP", e);
            throw new RuntimeException("Não foi possível inicializar FileService: falha na criação do diretório TEMP", e);
        }
    }

    public void saveFile(String fileName, byte[] fileData) {
        try {
            Path filePath = TEMP_PATH.resolve(fileName);
            Files.write(filePath, fileData);
            log.info("Arquivo salvo: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Erro ao salvar arquivo: {}", fileName, e);
            throw new RuntimeException("Falha ao salvar arquivo: " + fileName, e);
        }
    }

    public byte[] readFile(String fileName) {
        try {
            Path filePath = TEMP_PATH.resolve(fileName);
            if (!Files.exists(filePath)) {
                log.warn("Arquivo não encontrado: {}", filePath.toAbsolutePath());
                return null;
            }
            byte[] data = Files.readAllBytes(filePath);
            log.info("Arquivo lido: {} ({} bytes)", filePath.toAbsolutePath(), data.length);
            return data;
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", fileName, e);
            throw new RuntimeException("Falha ao ler arquivo: " + fileName, e);
        }
    }

    public void appendFile(String fileName, byte[] fileData) {
        try {
            Path filePath = TEMP_PATH.resolve(fileName);
            Files.write(filePath, fileData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            log.info("Dados adicionados ao arquivo: {} ({} bytes)", filePath.toAbsolutePath(), fileData.length);
        } catch (IOException e) {
            log.error("Erro ao adicionar dados ao arquivo: {}", fileName, e);
            throw new RuntimeException("Falha ao adicionar dados ao arquivo: " + fileName, e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = TEMP_PATH.resolve(fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Arquivo deletado: {}", filePath.toAbsolutePath());
            } else {
                log.warn("Arquivo não encontrado para deleção: {}", filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Erro ao deletar arquivo: {}", fileName, e);
            throw new RuntimeException("Falha ao deletar arquivo: " + fileName, e);
        }
    }

}
