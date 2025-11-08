package ru.itpark.sb;

import ru.itpark.sb.repository.DocumentRepository;
import ru.itpark.sb.service.DocumentService;
import ru.itpark.sb.service.EncryptionService;
import ru.itpark.sb.service.FileStorageService;
import ru.itpark.sb.ui.ConsoleUI;

import java.nio.file.Paths;

/**
 * Главный класс приложения для электронного документооборота
 */
public class Main {
    private static final String STORAGE_DIRECTORY = "documents_storage";

    public static void main(String[] args) {
        FileStorageService fileStorageService = new FileStorageService(STORAGE_DIRECTORY);
        DocumentRepository repository = new DocumentRepository(fileStorageService);
        EncryptionService encryptionService = new EncryptionService();
        DocumentService documentService = new DocumentService(repository, encryptionService);
        ConsoleUI consoleUI = new ConsoleUI(documentService);

        System.out.println("Директория хранения: " + Paths.get(STORAGE_DIRECTORY).toAbsolutePath());

        // Запуск приложения
        consoleUI.start();
    }
}