package ru.itpark.sb.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Сервис для шифрования и дешифрования документов
 */
public class EncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int SALT_LENGTH = 16;

    /**
     * Хеширование пароля с солью
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при хешировании пароля", e);
        }
    }

    /**
     * Проверка пароля
     */
    public boolean verifyPassword(String password, String passwordHash) {
        String computedHash = hashPassword(password);
        return computedHash.equals(passwordHash);
    }

    /**
     * Генерация ключа из пароля
     */
    private SecretKey generateKeyFromPassword(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] key = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(key, KEY_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации ключа из пароля", e);
        }
    }

    /**
     * Шифрование данных с использованием пароля
     */
    public byte[] encrypt(byte[] data, String password) {
        try {
            // Генерация соли и IV
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[GCM_IV_LENGTH];
            random.nextBytes(salt);
            random.nextBytes(iv);

            // Генерация ключа из пароля
            SecretKey key = generateKeyFromPassword(password, salt);

            // Инициализация шифра
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            // Шифрование данных
            byte[] encryptedData = cipher.doFinal(data);

            // Объединение соли, IV и зашифрованных данных
            byte[] result = new byte[SALT_LENGTH + GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(salt, 0, result, 0, SALT_LENGTH);
            System.arraycopy(iv, 0, result, SALT_LENGTH, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, result, SALT_LENGTH + GCM_IV_LENGTH, encryptedData.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании данных", e);
        }
    }

    /**
     * Дешифрование данных с использованием пароля
     */
    public byte[] decrypt(byte[] encryptedData, String password) {
        try {
            // Извлечение соли, IV и зашифрованных данных
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[encryptedData.length - SALT_LENGTH - GCM_IV_LENGTH];

            System.arraycopy(encryptedData, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(encryptedData, SALT_LENGTH, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, SALT_LENGTH + GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            // Генерация ключа из пароля
            SecretKey key = generateKeyFromPassword(password, salt);

            // Инициализация шифра для дешифрования
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            // Дешифрование данных
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при дешифровании данных. Проверьте пароль.", e);
        }
    }
}

