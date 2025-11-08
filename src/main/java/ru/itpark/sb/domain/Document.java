package ru.itpark.sb.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Модель документа в системе электронного документооборота
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Document {
    @EqualsAndHashCode.Include
    private String id;
    
    private String name;
    
    @Setter(AccessLevel.NONE)
    private byte[] encryptedContent;
    
    private String passwordHash;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    /**
     * Установка зашифрованного содержимого с автоматическим обновлением времени изменения
     */
    public void setEncryptedContent(byte[] encryptedContent) {
        this.encryptedContent = encryptedContent;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Конструктор для создания документа с автоматической установкой времени создания
     */
    public Document(String id, String name, byte[] encryptedContent, String passwordHash) {
        this.id = id;
        this.name = name;
        this.encryptedContent = encryptedContent;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
