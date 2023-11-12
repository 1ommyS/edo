package org.example.utils;

import org.example.entity.User;
import org.example.files.FileSearcherOS;

import java.io.File;
import java.util.Optional;

/**
 * @author 1ommy
 * @version 12.11.2023
 */
public class UserMenu extends Menu {
    public UserMenu() {
        this.fileSearcher = new FileSearcherOS();
    }

    @Override
    public void displayMenu() {
        System.out.println("Меню:");
        System.out.println("1. Показать мои задачи");
        System.out.println("2. Показать мои не выполненные задачи");
        System.out.println("3. Показать мои выполненные задачи");
        System.out.println("4. Подписать файл");
        System.out.println("5. Попробовать получить доступ к файлу");
        System.out.println("6. Выход");
    }

    @Override
    public void handle(User authenticatedUser) {
        int command = scanner.nextInt();

        switch (command) {
            case 1 -> userService.displayMyTasks(authenticatedUser);
            case 2 -> userService.displayMyNotCompletedTasks(authenticatedUser);
            case 3 -> userService.displayMyCompletedTasks(authenticatedUser);
            case 4 -> {
                InformationAboutSignedFile result = getInfo();
                if (result == null) return;
                signFileService.signFile(result.fileByName().getName(), result.keyWord());
            }
            case 5 -> {
                InformationAboutSignedFile result = getInfo();
                if (result == null) return;
                signFileService.tryToAccessFile(result.fileByName().getName(), result.keyWord());
            }
            case 6 -> {
                System.out.println("Пока пока");
                System.exit(0);
            }
        }
    }

    private InformationAboutSignedFile getInfo() {
        System.out.println("Введите имя файла:");
        Optional<File> fileByName = fileSearcher.findFileByName(scanner.nextLine());

        if (fileByName.isEmpty()) {
            System.out.println("Упс,такого файла нет");
            return null;
        }

        System.out.println("Введите ключевое слово для доступа к файлу");

        var keyWord = scanner.nextLine();
        InformationAboutSignedFile result = new InformationAboutSignedFile(fileByName.get(), keyWord);
        return result;
    }

    private record InformationAboutSignedFile(File fileByName, String keyWord) {
    }
}
