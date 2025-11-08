package ru.itpark.sb.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import ru.itpark.sb.domain.Document;
import ru.itpark.sb.service.FileStorageService;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Репозиторий для хранения документов в памяти (ConcurrentHashMap) и на диске
 */
@Slf4j
public class DocumentRepository {
    private final Map<String, Document> documents = new ConcurrentHashMap<>();
    private final FileStorageService fileStorageService;
    private final Gson gson;

    public DocumentRepository(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        loadDocumentsFromDisk();
    }

    /**
     * Загрузить документы с диска
     */
    private void loadDocumentsFromDisk() {
        try {
            String metadataJson = fileStorageService.loadMetadata();
            Type listType = new TypeToken<List<DocumentMetadata>>() {
            }.getType();
            List<DocumentMetadata> metadataList = gson.fromJson(metadataJson, listType);

            if (metadataList != null) {
                metadataList.stream()
                        .forEach(metadata -> {
                            Optional<byte[]> content = fileStorageService.loadDocumentContent(metadata.getId());
                            if (content.isPresent()) {
                                Document document = new Document(
                                        metadata.getId(),
                                        metadata.getName(),
                                        content.get(),
                                        metadata.getPasswordHash()
                                );
                                document.setCreatedAt(metadata.getCreatedAt());
                                document.setUpdatedAt(metadata.getUpdatedAt());
                                documents.put(metadata.getId(), document);
                            }
                        });
                log.info("Загружено {} документов с диска", documents.size());
            }
        } catch (Exception e) {
            log.error("Ошибка при загрузке документов с диска: {}", e.getMessage(), e);
        }
    }

    /**
     * Сохранить документ в память и на диск
     */
    public void save(Document document) {
        documents.put(document.getId(), document);
        saveToDisk(document);
        saveMetadataToDisk();
    }

    /**
     * Сохранить содержимое документа на диск
     */
    private void saveToDisk(Document document) {
        fileStorageService.saveDocumentContent(document.getId(), document.getEncryptedContent());
    }

    /**
     * Сохранить метаданные на диск
     */
    private void saveMetadataToDisk() {
        List<DocumentMetadata> metadataList = documents.values().stream()
                .map(this::toMetadata)
                .collect(Collectors.toList());
        String json = gson.toJson(metadataList);
        fileStorageService.saveMetadata(json);
    }

    /**
     * Преобразовать Document в DocumentMetadata
     */
    private DocumentMetadata toMetadata(Document document) {
        DocumentMetadata metadata = new DocumentMetadata();
        metadata.setId(document.getId());
        metadata.setName(document.getName());
        metadata.setPasswordHash(document.getPasswordHash());
        metadata.setCreatedAt(document.getCreatedAt());
        metadata.setUpdatedAt(document.getUpdatedAt());
        return metadata;
    }

    /**
     * Найти документ по ID
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    /**
     * Найти все документы используя Stream API
     */
    public List<Document> findAll() {
        return documents.values().stream()
                .sorted(Comparator.comparing(Document::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Удалить документ по ID
     */
    public boolean deleteById(String id) {
        Document removed = documents.remove(id);
        if (removed != null) {
            fileStorageService.deleteDocumentContent(id);
            saveMetadataToDisk();
            return true;
        }
        return false;
    }

    /**
     * Проверить существование документа
     */
    public boolean existsById(String id) {
        return documents.containsKey(id);
    }

    /**
     * Найти документы по имени (частичное совпадение) используя Stream API
     */
    public List<Document> findByNameContaining(String name) {
        String lowerName = name.toLowerCase();
        return documents.values().stream()
                .filter(doc -> doc.getName().toLowerCase().contains(lowerName))
                .sorted(Comparator.comparing(Document::getName))
                .collect(Collectors.toList());
    }

    /**
     * Найти документы по диапазону дат создания используя Stream API
     */
    public List<Document> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return documents.values().stream()
                .filter(doc -> {
                    LocalDateTime createdAt = doc.getCreatedAt();
                    return createdAt.isAfter(from) && createdAt.isBefore(to);
                })
                .sorted(Comparator.comparing(Document::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Получить документы, отсортированные по дате обновления используя Stream API
     */
    public List<Document> findAllSortedByUpdatedAt() {
        return documents.values().stream()
                .sorted(Comparator.comparing(Document::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Получить количество документов используя Stream API
     */
    public int count() {
        return (int) documents.values().stream().count();
    }

    /**
     * Класс для метаданных документа
     */
    private static class DocumentMetadata {
        private String id;
        private String name;
        private String passwordHash;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    /**
     * Адаптер для сериализации/десериализации LocalDateTime
     */
    private static class LocalDateTimeAdapter implements com.google.gson.JsonSerializer<LocalDateTime>,
            com.google.gson.JsonDeserializer<LocalDateTime> {
        @Override
        public com.google.gson.JsonElement serialize(LocalDateTime src, Type typeOfSrc,
                                                     com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(com.google.gson.JsonElement json, Type typeOfT,
                                         com.google.gson.JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString());
        }
    }
}
