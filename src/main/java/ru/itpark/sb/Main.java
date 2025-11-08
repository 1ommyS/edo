package ru.itpark.sb;

import ru.itpark.sb.repository.DocumentRepository;
import ru.itpark.sb.service.DocumentService;
import ru.itpark.sb.service.EncryptionService;
import ru.itpark.sb.ui.ConsoleUI;

/**
 * Главный класс приложения для электронного документооборота
 */
public class Main {
    public static void main(String[] args) {
        DocumentRepository repository = new DocumentRepository();
        EncryptionService encryptionService = new EncryptionService();
        DocumentService documentService = new DocumentService(repository, encryptionService);
        ConsoleUI consoleUI = new ConsoleUI(documentService);

        // Запуск приложения
        consoleUI.start();
    }
}