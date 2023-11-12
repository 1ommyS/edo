package org.example.utils;

import org.example.entity.User;

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

    }
}
