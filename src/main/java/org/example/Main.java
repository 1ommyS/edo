package org.example;/*
package org.example;

import lombok.val;
import org.example.files.FileSearcher;
import org.example.files.FileSearcherOS;

import java.io.File;

*/

import org.example.database.Database;
import org.example.entity.User;
import org.example.service.AuthorisationService;
import org.example.utils.AdminMenu;
import org.example.utils.Menu;
import org.example.utils.UserMenu;

import java.util.Scanner;

/**
 * @author 1ommy
 * @version 29.10.2023
 */

public class Main {
    public static void main(String[] args) throws Exception {
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
