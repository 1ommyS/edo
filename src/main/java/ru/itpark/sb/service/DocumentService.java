package ru.itpark.sb.service;

import lombok.RequiredArgsConstructor;
import ru.itpark.sb.domain.Document;
import ru.itpark.sb.repository.DocumentRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с документами (бизнес-логика)
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
     * Получить документ по ID с расшифровкой
     */
    public Optional<String> getDocumentContent(String id, String password) {
        Optional<Document> documentOpt = repository.findById(id);
        if (documentOpt.isEmpty()) {
            return Optional.empty();
        }

        Document document = documentOpt.get();

        // Проверка пароля
        if (!encryptionService.verifyPassword(password, document.getPasswordHash())) {
            throw new SecurityException("Неверный пароль");
        }

        // Дешифрование содержимого
        try {
            byte[] decryptedContent = encryptionService.decrypt(
                    document.getEncryptedContent(),
                    password
            );
            return Optional.of(new String(decryptedContent, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расшифровке документа: " + e.getMessage(), e);
        }
    }

    /**
     * Получить документ без расшифровки (метаданные)
     */
    public Optional<Document> getDocument(String id) {
        return repository.findById(id);
    }

    /**
     * Получить все документы
     */
    public List<Document> getAllDocuments() {
        return repository.findAll();
    }

    /**
     * Удалить документ
     */
    public boolean deleteDocument(String id) {
        return repository.deleteById(id);
    }

    /**
     * Поиск документов по имени
     */
    public List<Document> searchDocumentsByName(String name) {
        return repository.findByNameContaining(name);
    }

    /**
     * Обновить документ
     */
    public Document updateDocument(String id, String newContent, String password) {
        Optional<Document> documentOpt = repository.findById(id);
        if (documentOpt.isEmpty()) {
            throw new IllegalArgumentException("Документ с ID " + id + " не найден");
        }

        Document document = documentOpt.get();

        // Проверка пароля
        if (!encryptionService.verifyPassword(password, document.getPasswordHash())) {
            throw new SecurityException("Неверный пароль");
        }

        // Шифрование нового содержимого
        byte[] encryptedContent = encryptionService.encrypt(
                newContent.getBytes(StandardCharsets.UTF_8),
                password
        );

        document.setEncryptedContent(encryptedContent);
        repository.save(document);
        return document;
    }

    /**
     * Изменить пароль документа
     */
    public boolean changePassword(String id, String oldPassword, String newPassword) {
        Optional<Document> documentOpt = repository.findById(id);
        if (documentOpt.isEmpty()) {
            return false;
        }

        Document document = documentOpt.get();

        // Проверка старого пароля
        if (!encryptionService.verifyPassword(oldPassword, document.getPasswordHash())) {
            throw new SecurityException("Неверный старый пароль");
        }

        // Расшифровка с старым паролем
        byte[] decryptedContent = encryptionService.decrypt(
                document.getEncryptedContent(),
                oldPassword
        );

        // Шифрование с новым паролем
        byte[] newEncryptedContent = encryptionService.encrypt(decryptedContent, newPassword);
        String newPasswordHash = encryptionService.hashPassword(newPassword);

        document.setEncryptedContent(newEncryptedContent);
        document.setPasswordHash(newPasswordHash);
        repository.save(document);

        return true;
    }

    /**
     * Получить количество документов
     */
    public int getDocumentCount() {
        return repository.count();
    }
}

