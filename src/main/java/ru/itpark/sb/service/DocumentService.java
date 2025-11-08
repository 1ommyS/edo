package ru.itpark.sb.service;

import lombok.RequiredArgsConstructor;
import ru.itpark.sb.domain.Document;
import ru.itpark.sb.repository.DocumentRepository;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с документами (бизнес-логика) с использованием Stream API
 */
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository repository;
    private final EncryptionService encryptionService;

    /**
     * Сохранить документ с паролем
     */
    public Document saveDocument(String name, String content, String password) {
        String id = UUID.randomUUID().toString();
        String passwordHash = encryptionService.hashPassword(password);
        byte[] encryptedContent = encryptionService.encrypt(
                content.getBytes(StandardCharsets.UTF_8),
                password
        );

        Document document = new Document(id, name, encryptedContent, passwordHash);
        repository.save(document);
        return document;
    }

    /**
     * Получить документ по ID с расшифровкой используя Stream API и Optional
     */
    public Optional<String> getDocumentContent(String id, String password) {
        return repository.findById(id)
                .filter(doc -> encryptionService.verifyPassword(password, doc.getPasswordHash()))
                .map(doc -> {
                    try {
                        byte[] decryptedContent = encryptionService.decrypt(
                                doc.getEncryptedContent(),
                                password
                        );
                        return new String(decryptedContent, StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка при расшифровке документа: " + e.getMessage(), e);
                    }
                })
                .or(() -> {
                    // Проверка существования документа для более информативной ошибки
                    if (repository.existsById(id)) {
                        throw new SecurityException("Неверный пароль");
                    }
                    return Optional.empty();
                });
    }

    /**
     * Получить документ без расшифровки (метаданные)
     */
    public Optional<Document> getDocument(String id) {
        return repository.findById(id);
    }

    /**
     * Получить все документы используя Stream API
     */
    public List<Document> getAllDocuments() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Document::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Удалить документ
     */
    public boolean deleteDocument(String id) {
        return repository.deleteById(id);
    }

    /**
     * Поиск документов по имени используя Stream API
     */
    public List<Document> searchDocumentsByName(String name) {
        return repository.findByNameContaining(name);
    }

    /**
     * Обновить документ используя Stream API и Optional
     */
    public Document updateDocument(String id, String newContent, String password) {
        return repository.findById(id)
                .map(doc -> {
                    if (!encryptionService.verifyPassword(password, doc.getPasswordHash())) {
                        throw new SecurityException("Неверный пароль");
                    }
                    byte[] encryptedContent = encryptionService.encrypt(
                            newContent.getBytes(StandardCharsets.UTF_8),
                            password
                    );
                    doc.setEncryptedContent(encryptedContent);
                    repository.save(doc);
                    return doc;
                })
                .orElseThrow(() -> new IllegalArgumentException("Документ с ID " + id + " не найден"));
    }

    /**
     * Изменить пароль документа используя Stream API и Optional
     */
    public boolean changePassword(String id, String oldPassword, String newPassword) {
        return repository.findById(id)
                .map(doc -> {
                    if (!encryptionService.verifyPassword(oldPassword, doc.getPasswordHash())) {
                        throw new SecurityException("Неверный старый пароль");
                    }
                    byte[] decryptedContent = encryptionService.decrypt(
                            doc.getEncryptedContent(),
                            oldPassword
                    );
                    byte[] newEncryptedContent = encryptionService.encrypt(decryptedContent, newPassword);
                    String newPasswordHash = encryptionService.hashPassword(newPassword);
                    doc.setEncryptedContent(newEncryptedContent);
                    doc.setPasswordHash(newPasswordHash);
                    repository.save(doc);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Получить количество документов используя Stream API
     */
    public int getDocumentCount() {
        return repository.count();
    }
}
