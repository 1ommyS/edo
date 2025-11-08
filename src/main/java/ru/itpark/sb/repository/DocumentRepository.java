package ru.itpark.sb.repository;

import ru.itpark.sb.domain.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Репозиторий для хранения документов в памяти (ConcurrentHashMap)
 */
public class DocumentRepository {
    private final Map<String, Document> documents = new ConcurrentHashMap<>();

    /**
     * Сохранить документ
     */
    public void save(Document document) {
        documents.put(document.getId(), document);
    }

    /**
     * Найти документ по ID
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    /**
     * Найти все документы
     */
    public List<Document> findAll() {
        return new ArrayList<>(documents.values());
    }

    /**
     * Удалить документ по ID
     */
    public boolean deleteById(String id) {
        return documents.remove(id) != null;
    }

    /**
     * Проверить существование документа
     */
    public boolean existsById(String id) {
        return documents.containsKey(id);
    }

    /**
     * Найти документы по имени (частичное совпадение)
     */
    public List<Document> findByNameContaining(String name) {
        return documents.values().stream()
                .filter(doc -> doc.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    /**
     * Получить количество документов
     */
    public int count() {
        return documents.size();
    }
}

