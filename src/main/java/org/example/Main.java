package org.example;/*
package org.example;

import lombok.val;
import org.example.files.FileSearcher;
import org.example.files.FileSearcherOS;

import java.io.File;

*/

import org.example.database.Database;
import org.example.entity.User;
import org.example.enums.Role;
import org.example.service.AuthorisationService;
import org.example.utils.AdminMenu;
import org.example.utils.Menu;
import org.example.utils.UserMenu;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author 1ommy
 * @version 29.10.2023
 */

public class Main {
    public static void puk() {
        var users = new ArrayList<User>();
        users.add(new User(UUID.randomUUID(), "admin", "admin", Role.ADMIN));
        users.add(new User(UUID.randomUUID(), "developer", "developer", Role.DEVELOPER));

        Database database = new Database();
        database.writeUsersToFile("users.csv", users);
    }

    public static void main(String[] args) throws Exception {
        puk();
        Scanner scanner = new Scanner(System.in);
        User authorisedUser;
        Menu menu = null;

        System.out.println("""
                Добрый день!Вы попали в приложение для электронного документооборота!
                Чтобы продолжить, вам необходимо авторизоваться.
                """);

        while (true) {
            AuthorisationService authorisationService = new AuthorisationService(new Database());
            System.out.println("Введите логин и пароль");
            var login = scanner.nextLine();
            var password = scanner.nextLine();

            var resultOfAuthorisation = authorisationService.tryToAuthoriseUser(login, password);

            if (resultOfAuthorisation.getLeft()) {
                System.out.println("Вы успешно авторизовались");
                authorisedUser = resultOfAuthorisation.getRight();
                break;
            }
        }

        switch (authorisedUser.getRole()) {
            case ADMIN -> menu = new AdminMenu();
            case DEVELOPER -> menu = new UserMenu();
        }

        menu.displayMenu();

        menu.handle(authorisedUser);
    }
}
