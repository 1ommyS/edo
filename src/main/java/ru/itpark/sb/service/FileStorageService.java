package ru.itpark.sb.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Сервис для работы с файлами на диске
 */
@Slf4j
public class FileStorageService {
    private final Path storageDirectory;
    private final Path contentDirectory;
    private final Path metadataFile;

    public FileStorageService(String baseDirectory) {
        this.storageDirectory = Paths.get(baseDirectory);
        this.contentDirectory = storageDirectory.resolve("content");
        this.metadataFile = storageDirectory.resolve("metadata.json");
        initializeDirectories();
    }

    /**
     * Инициализация директорий для хранения
     */
    private void initializeDirectories() {
        try {
            Files.createDirectories(storageDirectory);
            Files.createDirectories(contentDirectory);
            if (!Files.exists(metadataFile)) {
                Files.createFile(metadataFile);
                Files.writeString(metadataFile, "[]");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при инициализации директорий: " + e.getMessage(), e);
        }
    }

    /**
     * Сохранить зашифрованное содержимое документа в файл
     */
    public void saveDocumentContent(String documentId, byte[] encryptedContent) {
        try {
            Path contentFile = contentDirectory.resolve(documentId + ".enc");
            Files.write(contentFile, encryptedContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.debug("Сохранено содержимое документа: {}", documentId);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении содержимого документа: " + e.getMessage(), e);
        }
    }

    /**
     * Загрузить зашифрованное содержимое документа из файла
     */
    public Optional<byte[]> loadDocumentContent(String documentId) {
        try {
            Path contentFile = contentDirectory.resolve(documentId + ".enc");
            if (!Files.exists(contentFile)) {
                return Optional.empty();
            }
            byte[] content = Files.readAllBytes(contentFile);
            log.debug("Загружено содержимое документа: {}", documentId);
            return Optional.of(content);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке содержимого документа: " + e.getMessage(), e);
        }
    }

    /**
     * Удалить файл с содержимым документа
     */
    public boolean deleteDocumentContent(String documentId) {
        try {
            Path contentFile = contentDirectory.resolve(documentId + ".enc");
            if (!Files.exists(contentFile)) {
                return false;
            }
            Files.delete(contentFile);
            log.debug("Удалено содержимое документа: {}", documentId);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при удалении содержимого документа: " + e.getMessage(), e);
        }
    }

    /**
     * Сохранить метаданные документов в JSON файл
     */
    public void saveMetadata(String json) {
        try {
            Files.writeString(metadataFile, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.debug("Метаданные сохранены");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении метаданных: " + e.getMessage(), e);
        }
    }

    /**
     * Загрузить метаданные документов из JSON файла
     */
    public String loadMetadata() {
        try {
            if (!Files.exists(metadataFile) || Files.size(metadataFile) == 0) {
                return "[]";
            }
            return Files.readString(metadataFile);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке метаданных: " + e.getMessage(), e);
        }
    }
}

