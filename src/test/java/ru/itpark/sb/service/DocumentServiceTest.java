package ru.itpark.sb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itpark.sb.domain.Document;
import ru.itpark.sb.repository.DocumentRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для DocumentService")
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @Mock
    private EncryptionService encryptionService;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(repository, encryptionService);
    }

    @Test
    @DisplayName("Должен сохранять документ с паролем")
    void shouldSaveDocument() {
        // given
        String name = "Test Document";
        String content = "Test Content";
        String password = "password123";
        String passwordHash = "hashedPassword";
        byte[] encryptedContent = "encrypted".getBytes();

        when(encryptionService.hashPassword(password)).thenReturn(passwordHash);
        when(encryptionService.encrypt(any(byte[].class), eq(password))).thenReturn(encryptedContent);

        // when
        Document document = documentService.saveDocument(name, content, password);

        // then
        assertThat(document).isNotNull();
        assertThat(document.getName()).isEqualTo(name);
        assertThat(document.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(document.getEncryptedContent()).isEqualTo(encryptedContent);
        
        verify(encryptionService).hashPassword(password);
        verify(encryptionService).encrypt(content.getBytes(StandardCharsets.UTF_8), password);
        verify(repository).save(any(Document.class));
    }

    @Test
    @DisplayName("Должен получать содержимое документа с правильным паролем")
    void shouldGetDocumentContentWithCorrectPassword() {
        // given
        String id = "doc1";
        String password = "password123";
        String passwordHash = "hashedPassword";
        String content = "Document Content";
        byte[] encryptedContent = "encrypted".getBytes();
        byte[] decryptedContent = content.getBytes(StandardCharsets.UTF_8);

        Document document = new Document(id, "Test", encryptedContent, passwordHash);
        when(repository.findById(id)).thenReturn(Optional.of(document));
        when(encryptionService.verifyPassword(password, passwordHash)).thenReturn(true);
        when(encryptionService.decrypt(encryptedContent, password)).thenReturn(decryptedContent);

        // when
        Optional<String> result = documentService.getDocumentContent(id, password);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(content);
        
        verify(repository).findById(id);
        verify(encryptionService).verifyPassword(password, passwordHash);
        verify(encryptionService).decrypt(encryptedContent, password);
    }

    @Test
    @DisplayName("Должен выбрасывать SecurityException при неверном пароле")
    void shouldThrowSecurityExceptionOnWrongPassword() {
        // given
        String id = "doc1";
        String password = "wrongPassword";
        String passwordHash = "hashedPassword";
        byte[] encryptedContent = "encrypted".getBytes();

        Document document = new Document(id, "Test", encryptedContent, passwordHash);
        when(repository.findById(id)).thenReturn(Optional.of(document));
        when(encryptionService.verifyPassword(password, passwordHash)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> documentService.getDocumentContent(id, password))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Неверный пароль");
        
        verify(repository).findById(id);
        verify(encryptionService).verifyPassword(password, passwordHash);
        verify(encryptionService, never()).decrypt(any(), any());
    }

    @Test
    @DisplayName("Должен возвращать пустой Optional для несуществующего документа")
    void shouldReturnEmptyForNonExistentDocument() {
        // given
        String id = "non-existent";
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<String> result = documentService.getDocumentContent(id, "password");

        // then
        assertThat(result).isEmpty();
        verify(repository).findById(id);
        verify(encryptionService, never()).verifyPassword(any(), any());
    }

    @Test
    @DisplayName("Должен получать документ без расшифровки")
    void shouldGetDocument() {
        // given
        String id = "doc1";
        Document document = new Document(id, "Test", "encrypted".getBytes(), "hash");
        when(repository.findById(id)).thenReturn(Optional.of(document));

        // when
        Optional<Document> result = documentService.getDocument(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(document);
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Должен возвращать все документы")
    void shouldGetAllDocuments() {
        // given
        Document doc1 = new Document("1", "Doc1", "enc1".getBytes(), "hash1");
        Document doc2 = new Document("2", "Doc2", "enc2".getBytes(), "hash2");
        List<Document> documents = List.of(doc1, doc2);
        when(repository.findAll()).thenReturn(documents);

        // when
        List<Document> result = documentService.getAllDocuments();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(documents);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Должен удалять документ")
    void shouldDeleteDocument() {
        // given
        String id = "doc1";
        when(repository.deleteById(id)).thenReturn(true);

        // when
        boolean result = documentService.deleteDocument(id);

        // then
        assertThat(result).isTrue();
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Должен искать документы по имени")
    void shouldSearchDocumentsByName() {
        // given
        String searchName = "Java";
        Document doc1 = new Document("1", "Java Tutorial", "enc1".getBytes(), "hash1");
        List<Document> documents = List.of(doc1);
        when(repository.findByNameContaining(searchName)).thenReturn(documents);

        // when
        List<Document> result = documentService.searchDocumentsByName(searchName);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyInAnyOrderElementsOf(documents);
        verify(repository).findByNameContaining(searchName);
    }

    @Test
    @DisplayName("Должен обновлять документ с правильным паролем")
    void shouldUpdateDocumentWithCorrectPassword() {
        // given
        String id = "doc1";
        String newContent = "New Content";
        String password = "password123";
        String passwordHash = "hashedPassword";
        byte[] oldEncryptedContent = "oldEncrypted".getBytes();
        byte[] newEncryptedContent = "newEncrypted".getBytes();

        Document document = new Document(id, "Test", oldEncryptedContent, passwordHash);
        when(repository.findById(id)).thenReturn(Optional.of(document));
        when(encryptionService.verifyPassword(password, passwordHash)).thenReturn(true);
        when(encryptionService.encrypt(newContent.getBytes(StandardCharsets.UTF_8), password))
                .thenReturn(newEncryptedContent);

        // when
        Document updated = documentService.updateDocument(id, newContent, password);

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.getEncryptedContent()).isEqualTo(newEncryptedContent);
        
        verify(repository).findById(id);
        verify(encryptionService).verifyPassword(password, passwordHash);
        verify(encryptionService).encrypt(newContent.getBytes(StandardCharsets.UTF_8), password);
        verify(repository).save(document);
    }

    @Test
    @DisplayName("Должен выбрасывать IllegalArgumentException при обновлении несуществующего документа")
    void shouldThrowExceptionWhenUpdatingNonExistentDocument() {
        // given
        String id = "non-existent";
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> documentService.updateDocument(id, "content", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найден");
        
        verify(repository).findById(id);
        verify(encryptionService, never()).verifyPassword(any(), any());
    }

    @Test
    @DisplayName("Должен выбрасывать SecurityException при обновлении с неверным паролем")
    void shouldThrowSecurityExceptionWhenUpdatingWithWrongPassword() {
        // given
        String id = "doc1";
        String password = "wrongPassword";
        String passwordHash = "hashedPassword";
        byte[] encryptedContent = "encrypted".getBytes();

        Document document = new Document(id, "Test", encryptedContent, passwordHash);
        when(repository.findById(id)).thenReturn(Optional.of(document));
        when(encryptionService.verifyPassword(password, passwordHash)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> documentService.updateDocument(id, "newContent", password))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Неверный пароль");
        
        verify(repository).findById(id);
        verify(encryptionService).verifyPassword(password, passwordHash);
        verify(encryptionService, never()).encrypt(any(), any());
    }

    @Test
    @DisplayName("Должен изменять пароль документа")
    void shouldChangePassword() {
        // given
        String id = "doc1";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String oldPasswordHash = "oldHash";
        String newPasswordHash = "newHash";
        byte[] oldEncryptedContent = "oldEncrypted".getBytes();
        byte[] decryptedContent = "decrypted".getBytes();
        byte[] newEncryptedContent = "newEncrypted".getBytes();

        Document document = new Document(id, "Test", oldEncryptedContent, oldPasswordHash);
        when(repository.findById(id)).thenReturn(Optional.of(document));
        when(encryptionService.verifyPassword(oldPassword, oldPasswordHash)).thenReturn(true);
        when(encryptionService.decrypt(oldEncryptedContent, oldPassword)).thenReturn(decryptedContent);
        when(encryptionService.encrypt(decryptedContent, newPassword)).thenReturn(newEncryptedContent);
        when(encryptionService.hashPassword(newPassword)).thenReturn(newPasswordHash);

        // when
        boolean result = documentService.changePassword(id, oldPassword, newPassword);

        // then
        assertThat(result).isTrue();
        assertThat(document.getPasswordHash()).isEqualTo(newPasswordHash);
        assertThat(document.getEncryptedContent()).isEqualTo(newEncryptedContent);
        
        verify(repository).findById(id);
        verify(encryptionService).verifyPassword(oldPassword, oldPasswordHash);
        verify(encryptionService).decrypt(oldEncryptedContent, oldPassword);
        verify(encryptionService).encrypt(decryptedContent, newPassword);
        verify(encryptionService).hashPassword(newPassword);
        verify(repository).save(document);
    }

    @Test
    @DisplayName("Должен возвращать false при изменении пароля несуществующего документа")
    void shouldReturnFalseWhenChangingPasswordOfNonExistentDocument() {
        // given
        String id = "non-existent";
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when
        boolean result = documentService.changePassword(id, "old", "new");

        // then
        assertThat(result).isFalse();
        verify(repository).findById(id);
        verify(encryptionService, never()).verifyPassword(any(), any());
    }

    @Test
    @DisplayName("Должен выбрасывать SecurityException при изменении пароля с неверным старым паролем")
    void shouldThrowSecurityExceptionWhenChangingPasswordWithWrongOldPassword() {
        // given
        String id = "doc1";
        String oldPassword = "wrongPassword";
        String oldPasswordHash = "hashedPassword";
        byte[] encryptedContent = "encrypted".getBytes();

        Document document = new Document(id, "Test", encryptedContent, oldPasswordHash);
        when(repository.findById(id)).thenReturn(Optional.of(document));
        when(encryptionService.verifyPassword(oldPassword, oldPasswordHash)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> documentService.changePassword(id, oldPassword, "newPassword"))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Неверный старый пароль");
        
        verify(repository).findById(id);
        verify(encryptionService).verifyPassword(oldPassword, oldPasswordHash);
        verify(encryptionService, never()).decrypt(any(), any());
    }

    @Test
    @DisplayName("Должен возвращать количество документов")
    void shouldGetDocumentCount() {
        // given
        when(repository.count()).thenReturn(5);

        // when
        int count = documentService.getDocumentCount();

        // then
        assertThat(count).isEqualTo(5);
        verify(repository).count();
    }
}

