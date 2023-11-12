package org.example.utils;

import org.example.dto.TaskDTO;
import org.example.entity.User;
import org.example.service.TaskService;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * @author 1ommy
 * @version 12.11.2023
 */
public class AdminMenu extends Menu {
    @Override
    public void displayMenu() {
        System.out.println("Меню:");
        System.out.println("1. Добавить задачу");
        System.out.println("2. Показать все задачи");
        System.out.println("3. Присвоить задачу человеку");
        System.out.println("4. Выход");
    }

    @Override
    public void handle(User authenticatedUser) {
        int command = scanner.nextInt();

        switch (command) {
            case 1 -> {
                System.out.println("Введите заголовок: ");
                String title = scanner.nextLine();
                System.out.println("Введлите описание: ");
                String description = scanner.nextLine();
                TaskDTO taskDTO = new TaskDTO(UUID.randomUUID(),
                        title,
                        description,
                        null);
                taskService.createTask(taskDTO);
            }
            case 2 -> {
                taskService.getTasks().parallelStream().forEach(System.out::println);
            }
            case 3 -> {
                System.out.println("Введите заголовок: ");
                String title = scanner.nextLine();
                System.out.println("Введлите имя человека: ");
                String name = scanner.nextLine();
                taskService.assignTask(title, name);

            }
            case 4 -> {
            System.exit(0);
            }
        }
    }

    private  InformationAboutSignedFile getInfo() {
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
