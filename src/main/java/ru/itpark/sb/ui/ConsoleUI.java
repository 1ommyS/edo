package ru.itpark.sb.ui;

import lombok.RequiredArgsConstructor;
import ru.itpark.sb.domain.Document;
import ru.itpark.sb.service.DocumentService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Консольный пользовательский интерфейс
 */
@RequiredArgsConstructor
public class ConsoleUI {
    private final DocumentService documentService;
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== Система электронного документооборота ===");
        System.out.println("Добро пожаловать!");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createDocument();
                    case "2" -> viewDocument();
                    case "3" -> listDocuments();
                    case "4" -> searchDocuments();
                    case "5" -> updateDocument();
                    case "6" -> deleteDocument();
                    case "7" -> changePassword();
                    case "8" -> showStatistics();
                    case "0" -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (SecurityException e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("ОШИБКА: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\nНажмите Enter для продолжения...");
            scanner.nextLine();
        }
    }

    private void printMenu() {
        System.out.println("\n=== МЕНЮ ===");
        System.out.println("1. Создать документ");
        System.out.println("2. Просмотреть документ");
        System.out.println("3. Список всех документов");
        System.out.println("4. Поиск документов");
        System.out.println("5. Обновить документ");
        System.out.println("6. Удалить документ");
        System.out.println("7. Изменить пароль документа");
        System.out.println("8. Статистика");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createDocument() {
        System.out.println("\n=== Создание документа ===");
        System.out.print("Введите название документа: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Название не может быть пустым!");
            return;
        }

        System.out.print("Введите содержимое документа: ");
        String content = scanner.nextLine().trim();
        if (content.isEmpty()) {
            System.out.println("Содержимое не может быть пустым!");
            return;
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Пароль не может быть пустым!");
            return;
        }

        Document document = documentService.saveDocument(name, content, password);
        System.out.println("Документ успешно создан!");
        System.out.println("ID: " + document.getId());
        System.out.println("Название: " + document.getName());
        System.out.println("Создан: " + document.getCreatedAt());
    }

    private void viewDocument() {
        System.out.println("\n=== Просмотр документа ===");
        System.out.print("Введите ID документа: ");
        String id = scanner.nextLine().trim();

        Optional<Document> documentOpt = documentService.getDocument(id);
        if (documentOpt.isEmpty()) {
            System.out.println("Документ с ID " + id + " не найден!");
            return;
        }

        Document document = documentOpt.get();
        System.out.println("ID: " + document.getId());
        System.out.println("Название: " + document.getName());
        System.out.println("Создан: " + document.getCreatedAt());
        System.out.println("Обновлен: " + document.getUpdatedAt());

        System.out.print("Введите пароль для просмотра содержимого: ");
        String password = scanner.nextLine().trim();

        Optional<String> contentOpt = documentService.getDocumentContent(id, password);
        if (contentOpt.isPresent()) {
            System.out.println("\n=== Содержимое документа ===");
            System.out.println(contentOpt.get());
        }
    }

    private void listDocuments() {
        System.out.println("\n=== Список документов ===");
        List<Document> documents = documentService.getAllDocuments();

        if (documents.isEmpty()) {
            System.out.println("Документы не найдены.");
            return;
        }

        System.out.println("Всего документов: " + documents.size());
        System.out.println();
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            System.out.println((i + 1) + ". " + doc.getName() + " (ID: " + doc.getId() + ")");
            System.out.println("   Создан: " + doc.getCreatedAt());
        }
    }

    private void searchDocuments() {
        System.out.println("\n=== Поиск документов ===");
        System.out.print("Введите название для поиска: ");
        String searchName = scanner.nextLine().trim();

        List<Document> documents = documentService.searchDocumentsByName(searchName);

        if (documents.isEmpty()) {
            System.out.println("Документы не найдены.");
            return;
        }

        System.out.println("Найдено документов: " + documents.size());
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            System.out.println((i + 1) + ". " + doc.getName() + " (ID: " + doc.getId() + ")");
        }
    }

    private void updateDocument() {
        System.out.println("\n=== Обновление документа ===");
        System.out.print("Введите ID документа: ");
        String id = scanner.nextLine().trim();

        Optional<Document> documentOpt = documentService.getDocument(id);
        if (documentOpt.isEmpty()) {
            System.out.println("Документ с ID " + id + " не найден!");
            return;
        }

        Document document = documentOpt.get();
        System.out.println("Текущий документ: " + document.getName());

        System.out.print("Введите новый текст документа: ");
        String newContent = scanner.nextLine().trim();
        if (newContent.isEmpty()) {
            System.out.println("Содержимое не может быть пустым!");
            return;
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        Document updated = documentService.updateDocument(id, newContent, password);
        System.out.println("Документ успешно обновлен!");
        System.out.println("Обновлен: " + updated.getUpdatedAt());
    }

    private void deleteDocument() {
        System.out.println("\n=== Удаление документа ===");
        System.out.print("Введите ID документа: ");
        String id = scanner.nextLine().trim();

        Optional<Document> documentOpt = documentService.getDocument(id);
        if (documentOpt.isEmpty()) {
            System.out.println("Документ с ID " + id + " не найден!");
            return;
        }

        Document document = documentOpt.get();
        System.out.println("Вы собираетесь удалить документ: " + document.getName());
        System.out.print("Подтвердите удаление (да/нет): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if ("да".equals(confirmation) || "yes".equals(confirmation) || "y".equals(confirmation)) {
            boolean deleted = documentService.deleteDocument(id);
            if (deleted) {
                System.out.println("Документ успешно удален!");
            } else {
                System.out.println("Ошибка при удалении документа!");
            }
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    private void changePassword() {
        System.out.println("\n=== Изменение пароля документа ===");
        System.out.print("Введите ID документа: ");
        String id = scanner.nextLine().trim();

        Optional<Document> documentOpt = documentService.getDocument(id);
        if (documentOpt.isEmpty()) {
            System.out.println("Документ с ID " + id + " не найден!");
            return;
        }

        System.out.print("Введите старый пароль: ");
        String oldPassword = scanner.nextLine().trim();

        System.out.print("Введите новый пароль: ");
        String newPassword = scanner.nextLine().trim();
        if (newPassword.isEmpty()) {
            System.out.println("Новый пароль не может быть пустым!");
            return;
        }

        boolean changed = documentService.changePassword(id, oldPassword, newPassword);
        if (changed) {
            System.out.println("Пароль успешно изменен!");
        } else {
            System.out.println("Ошибка при изменении пароля!");
        }
    }

    private void showStatistics() {
        System.out.println("\n=== Статистика ===");
        int count = documentService.getDocumentCount();
        System.out.println("Всего документов в системе: " + count);
    }
}

