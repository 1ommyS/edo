package ru.itpark.sb.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itpark.sb.domain.Document;
import ru.itpark.sb.service.FileStorageService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для DocumentRepository")
class DocumentRepositoryTest {

    @Mock
    private FileStorageService fileStorageService;

    private DocumentRepository repository;

    @BeforeEach
    void setUp() {
        // Мокаем загрузку метаданных при инициализации
        when(fileStorageService.loadMetadata()).thenReturn("[]");
        repository = new DocumentRepository(fileStorageService);
    }

    @Test
    @DisplayName("Должен сохранять документ")
    void shouldSaveDocument() {
        // given
        Document document = createTestDocument("1", "Test Document");

        // when
        repository.save(document);

        // then
        Optional<Document> found = repository.findById("1");
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("1");
        assertThat(found.get().getName()).isEqualTo("Test Document");
        
        // Проверяем, что файловый сервис был вызван
        verify(fileStorageService, atLeastOnce()).saveDocumentContent(anyString(), any(byte[].class));
        verify(fileStorageService, atLeastOnce()).saveMetadata(anyString());
    }

    @Test
    @DisplayName("Должен находить документ по ID")
    void shouldFindDocumentById() {
        // given
        Document document = createTestDocument("1", "Test Document");
        repository.save(document);

        // when
        Optional<Document> found = repository.findById("1");

        // then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(document);
    }

    @Test
    @DisplayName("Должен возвращать пустой Optional для несуществующего документа")
    void shouldReturnEmptyForNonExistentDocument() {
        // when
        Optional<Document> found = repository.findById("non-existent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен возвращать все документы")
    void shouldFindAllDocuments() {
        // given
        Document doc1 = createTestDocument("1", "Document 1");
        Document doc2 = createTestDocument("2", "Document 2");
        Document doc3 = createTestDocument("3", "Document 3");
        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        // when
        List<Document> allDocuments = repository.findAll();

        // then
        assertThat(allDocuments).hasSize(3);
        assertThat(allDocuments).containsExactlyInAnyOrder(doc1, doc2, doc3);
    }

    @Test
    @DisplayName("Должен возвращать пустой список когда нет документов")
    void shouldReturnEmptyListWhenNoDocuments() {
        // when
        List<Document> allDocuments = repository.findAll();

        // then
        assertThat(allDocuments).isEmpty();
    }

    @Test
    @DisplayName("Должен удалять документ по ID")
    void shouldDeleteDocumentById() {
        // given
        Document document = createTestDocument("1", "Test Document");
        repository.save(document);
        clearInvocations(fileStorageService); // Очищаем инвокации после save для чистой проверки

        // when
        boolean deleted = repository.deleteById("1");

        // then
        assertThat(deleted).isTrue();
        assertThat(repository.findById("1")).isEmpty();
        
        // Проверяем, что файловый сервис был вызван для удаления
        verify(fileStorageService).deleteDocumentContent("1");
        verify(fileStorageService).saveMetadata(anyString());
    }

    @Test
    @DisplayName("Должен возвращать false при удалении несуществующего документа")
    void shouldReturnFalseWhenDeletingNonExistentDocument() {
        // when
        boolean deleted = repository.deleteById("non-existent");

        // then
        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("Должен проверять существование документа")
    void shouldCheckDocumentExistence() {
        // given
        Document document = createTestDocument("1", "Test Document");

        // when & then
        assertThat(repository.existsById("1")).isFalse();
        
        repository.save(document);
        assertThat(repository.existsById("1")).isTrue();
    }

    @Test
    @DisplayName("Должен искать документы по имени (частичное совпадение)")
    void shouldFindDocumentsByNameContaining() {
        // given
        Document doc1 = createTestDocument("1", "Java Tutorial");
        Document doc2 = createTestDocument("2", "JavaScript Guide");
        Document doc3 = createTestDocument("3", "Python Basics");
        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        // when
        List<Document> found = repository.findByNameContaining("Java");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).containsExactlyInAnyOrder(doc1, doc2);
    }

    @Test
    @DisplayName("Должен искать документы по имени без учета регистра")
    void shouldFindDocumentsByNameCaseInsensitive() {
        // given
        Document doc1 = createTestDocument("1", "Java Tutorial");
        Document doc2 = createTestDocument("2", "JAVASCRIPT GUIDE");
        repository.save(doc1);
        repository.save(doc2);

        // when
        List<Document> found = repository.findByNameContaining("java");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).containsExactlyInAnyOrder(doc1, doc2);
    }

    @Test
    @DisplayName("Должен возвращать пустой список при поиске несуществующего имени")
    void shouldReturnEmptyListForNonExistentName() {
        // given
        Document doc1 = createTestDocument("1", "Java Tutorial");
        repository.save(doc1);

        // when
        List<Document> found = repository.findByNameContaining("Python");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен возвращать правильное количество документов")
    void shouldReturnCorrectCount() {
        // given
        assertThat(repository.count()).isEqualTo(0);

        // when
        repository.save(createTestDocument("1", "Doc 1"));
        repository.save(createTestDocument("2", "Doc 2"));
        repository.save(createTestDocument("3", "Doc 3"));

        // then
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен обновлять существующий документ при сохранении с тем же ID")
    void shouldUpdateDocumentWhenSavingWithSameId() {
        // given
        Document document1 = createTestDocument("1", "Original Name");
        repository.save(document1);

        // when
        Document document2 = createTestDocument("1", "Updated Name");
        repository.save(document2);

        // then
        Optional<Document> found = repository.findById("1");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Name");
        assertThat(repository.count()).isEqualTo(1);
    }

    private Document createTestDocument(String id, String name) {
        return new Document(id, name, "encrypted".getBytes(), "passwordHash");
    }
}

