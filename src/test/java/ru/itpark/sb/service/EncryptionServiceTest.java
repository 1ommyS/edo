package ru.itpark.sb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тесты для EncryptionService")
class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
    }

    @Test
    @DisplayName("Должен хешировать пароль корректно")
    void shouldHashPassword() {
        // given
        String password = "testPassword123";

        // when
        String hash = encryptionService.hashPassword(password);

        // then
        assertThat(hash).isNotEmpty();
        assertThat(hash).isNotEqualTo(password);
    }

    @Test
    @DisplayName("Должен хешировать одинаковые пароли одинаково")
    void shouldHashSamePasswordSameWay() {
        // given
        String password = "samePassword";

        // when
        String hash1 = encryptionService.hashPassword(password);
        String hash2 = encryptionService.hashPassword(password);

        // then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Должен хешировать разные пароли по-разному")
    void shouldHashDifferentPasswordsDifferently() {
        // given
        String password1 = "password1";
        String password2 = "password2";

        // when
        String hash1 = encryptionService.hashPassword(password1);
        String hash2 = encryptionService.hashPassword(password2);

        // then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Должен проверять пароль корректно")
    void shouldVerifyPassword() {
        // given
        String password = "testPassword";
        String hash = encryptionService.hashPassword(password);

        // when
        boolean isValid = encryptionService.verifyPassword(password, hash);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Должен отклонять неверный пароль")
    void shouldRejectInvalidPassword() {
        // given
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hash = encryptionService.hashPassword(correctPassword);

        // when
        boolean isValid = encryptionService.verifyPassword(wrongPassword, hash);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Должен шифровать и дешифровать данные корректно")
    void shouldEncryptAndDecryptData() {
        // given
        String originalData = "This is a test message for encryption";
        String password = "testPassword123";
        byte[] data = originalData.getBytes();

        // when
        byte[] encrypted = encryptionService.encrypt(data, password);
        byte[] decrypted = encryptionService.decrypt(encrypted, password);

        // then
        assertThat(encrypted).isNotEmpty();
        assertThat(encrypted).isNotEqualTo(data);
        assertThat(decrypted).isEqualTo(data);
        assertThat(new String(decrypted)).isEqualTo(originalData);
    }

    @Test
    @DisplayName("Должен шифровать одинаковые данные по-разному (разные соли)")
    void shouldEncryptSameDataDifferently() {
        // given
        String data = "Same data";
        String password = "password";
        byte[] dataBytes = data.getBytes();

        // when
        byte[] encrypted1 = encryptionService.encrypt(dataBytes, password);
        byte[] encrypted2 = encryptionService.encrypt(dataBytes, password);

        // then
        assertThat(encrypted1).isNotEqualTo(encrypted2);
        
        // Но дешифровка должна давать одинаковый результат
        byte[] decrypted1 = encryptionService.decrypt(encrypted1, password);
        byte[] decrypted2 = encryptionService.decrypt(encrypted2, password);
        assertThat(decrypted1).isEqualTo(decrypted2);
        assertThat(decrypted1).isEqualTo(dataBytes);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение при неверном пароле при дешифровке")
    void shouldThrowExceptionOnWrongPassword() {
        // given
        String data = "Secret data";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        byte[] dataBytes = data.getBytes();
        byte[] encrypted = encryptionService.encrypt(dataBytes, correctPassword);

        // when & then
        assertThatThrownBy(() -> encryptionService.decrypt(encrypted, wrongPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ошибка при дешифровании данных");
    }

    @Test
    @DisplayName("Должен работать с пустыми данными")
    void shouldWorkWithEmptyData() {
        // given
        byte[] emptyData = new byte[0];
        String password = "password";

        // when
        byte[] encrypted = encryptionService.encrypt(emptyData, password);
        byte[] decrypted = encryptionService.decrypt(encrypted, password);

        // then
        assertThat(decrypted).isEmpty();
        assertThat(decrypted).isEqualTo(emptyData);
    }

    @Test
    @DisplayName("Должен работать с большими данными")
    void shouldWorkWithLargeData() {
        // given
        StringBuilder largeDataBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeDataBuilder.append("A");
        }
        String largeData = largeDataBuilder.toString();
        String password = "password";
        byte[] dataBytes = largeData.getBytes();

        // when
        byte[] encrypted = encryptionService.encrypt(dataBytes, password);
        byte[] decrypted = encryptionService.decrypt(encrypted, password);

        // then
        assertThat(decrypted).isEqualTo(dataBytes);
        assertThat(new String(decrypted)).isEqualTo(largeData);
    }

    @Test
    @DisplayName("Должен работать с различными символами")
    void shouldWorkWithSpecialCharacters() {
        // given
        String data = "Тест с кириллицей! @#$%^&*() 1234567890";
        String password = "пароль123";
        byte[] dataBytes = data.getBytes();

        // when
        byte[] encrypted = encryptionService.encrypt(dataBytes, password);
        byte[] decrypted = encryptionService.decrypt(encrypted, password);

        // then
        assertThat(decrypted).isEqualTo(dataBytes);
        assertThat(new String(decrypted)).isEqualTo(data);
    }
}

