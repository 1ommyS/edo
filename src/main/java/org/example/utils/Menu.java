package org.example.utils;

import org.example.database.Database;
import org.example.entity.User;
import org.example.files.FileSearcher;
import org.example.service.SignFileService;
import org.example.service.TaskService;
import org.example.service.UserService;

import java.util.Scanner;

/**
 * @author 1ommy
 * @version 12.11.2023
 */
public abstract class Menu {
    protected Scanner scanner = new Scanner(System.in);
    protected TaskService taskService = new TaskService(new Database());
    protected UserService userService = new UserService(taskService);
    protected FileSearcher fileSearcher;
    protected SignFileService signFileService = new SignFileService(new Database());

    public abstract void displayMenu();

    public abstract void handle(User authenticatedUser);
}
