package org.example.service;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.HmacUtils;
import org.example.database.Database;
import org.example.entity.User;
import org.modelmapper.internal.Pair;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.codec.digest.HmacAlgorithms.HMAC_SHA_224;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
@AllArgsConstructor
public class AuthorisationService {
    private final String secret = "secret";
    private final Database database;
    private final String table = "users";


    public Pair<Boolean, User> tryToAuthoriseUser(String name, String password) {
        var users = database.readUsers(table);
        User user = users
                .parallelStream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> {
                    System.out.println("AuthorisationService.tryToAuthoriseUser | НЕ смогли найти пользователя");
                    return new IllegalArgumentException("Такого пользователя нет");
                });

        String hashedPassword = new HmacUtils(HMAC_SHA_224, secret.getBytes()).hmacHex(password);

        return Pair.of(hashedPassword.equals(user.getPassword()), user);
    }

    public void createUser(String name, String password) {
        UUID uuid = UUID.randomUUID();

        String userPassword = new HmacUtils(HMAC_SHA_224, secret.getBytes()).hmacHex(password);

        User newUser = User.builder()
                .id(uuid)
                .password(userPassword)
                .name(name)
                .build();

        database.insertIntoTable(table, newUser.toString());
    }
    public List<User> getUsers() {
        return database.readUsers(table);
    }
}
