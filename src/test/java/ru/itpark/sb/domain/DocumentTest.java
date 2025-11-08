package ru.itpark.sb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты для Document")
class DocumentTest {

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document("1", "Test Document", "encrypted".getBytes(), "passwordHash");
    }

    @Test
    @DisplayName("Должен создавать документ с правильными полями")
    void shouldCreateDocumentWithCorrectFields() {
        // given & when
        Document doc = new Document("1", "Test", "enc".getBytes(), "hash");

        // then
        assertThat(doc.getId()).isEqualTo("1");
        assertThat(doc.getName()).isEqualTo("Test");
        assertThat(doc.getEncryptedContent()).isEqualTo("enc".getBytes());
        assertThat(doc.getPasswordHash()).isEqualTo("hash");
        assertThat(doc.getCreatedAt()).isNotNull();
        assertThat(doc.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен создавать документ через конструктор без параметров")
    void shouldCreateDocumentWithNoArgsConstructor() {
        // when
        Document doc = new Document();
        doc.setId("1");
        doc.setName("Test");

        // then
        assertThat(doc.getId()).isEqualTo("1");
        assertThat(doc.getName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("Должен устанавливать зашифрованное содержимое и обновлять время")
    void shouldSetEncryptedContentAndUpdateTime() throws InterruptedException {
        // given
        LocalDateTime originalUpdatedAt = document.getUpdatedAt();
        byte[] newContent = "newEncrypted".getBytes();

        // when
        Thread.sleep(10); // Небольшая задержка для проверки времени
        document.setEncryptedContent(newContent);

        // then
        assertThat(document.getEncryptedContent()).isEqualTo(newContent);
        assertThat(document.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Должен правильно сравнивать документы по ID")
    void shouldCompareDocumentsById() {
        // given
        Document doc1 = new Document("1", "Doc1", "enc1".getBytes(), "hash1");
        Document doc2 = new Document("1", "Doc2", "enc2".getBytes(), "hash2");
        Document doc3 = new Document("2", "Doc1", "enc1".getBytes(), "hash1");

        // then
        assertThat(doc1).isEqualTo(doc2);
        assertThat(doc1).isNotEqualTo(doc3);
        assertThat(doc1.hashCode()).isEqualTo(doc2.hashCode());
    }

    @Test
    @DisplayName("Должен генерировать правильный hashCode")
    void shouldGenerateCorrectHashCode() {
        // given
        Document doc1 = new Document("1", "Doc1", "enc1".getBytes(), "hash1");
        Document doc2 = new Document("1", "Doc2", "enc2".getBytes(), "hash2");

        // then
        assertThat(doc1.hashCode()).isEqualTo(doc2.hashCode());
    }

    @Test
    @DisplayName("Должен генерировать правильный toString")
    void shouldGenerateCorrectToString() {
        // when
        String toString = document.toString();

        // then
        assertThat(toString).contains("Document");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("name=Test Document");
        assertThat(toString).contains("createdAt=");
        assertThat(toString).contains("updatedAt=");
    }

    @Test
    @DisplayName("Должен устанавливать и получать все поля")
    void shouldSetAndGetAllFields() {
        // given
        Document doc = new Document();
        LocalDateTime now = LocalDateTime.now();

        // when
        doc.setId("2");
        doc.setName("New Name");
        doc.setPasswordHash("newHash");
        doc.setCreatedAt(now);
        doc.setUpdatedAt(now);

        // then
        assertThat(doc.getId()).isEqualTo("2");
        assertThat(doc.getName()).isEqualTo("New Name");
        assertThat(doc.getPasswordHash()).isEqualTo("newHash");
        assertThat(doc.getCreatedAt()).isEqualTo(now);
        assertThat(doc.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Должен автоматически устанавливать время создания при создании через конструктор")
    void shouldSetCreationTimeAutomatically() throws InterruptedException {
        // given
        LocalDateTime before = LocalDateTime.now();

        // when
        Thread.sleep(10);
        Document doc = new Document("1", "Test", "enc".getBytes(), "hash");
        Thread.sleep(10);
        LocalDateTime after = LocalDateTime.now();

        // then
        assertThat(doc.getCreatedAt()).isAfter(before);
        assertThat(doc.getCreatedAt()).isBefore(after);
        assertThat(doc.getUpdatedAt()).isAfter(before);
        assertThat(doc.getUpdatedAt()).isBefore(after);
    }
}

