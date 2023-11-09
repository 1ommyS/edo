package org.example.mappers;

import org.example.entity.User;
import org.example.enums.Role;

import java.util.UUID;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
public class UserMapper {
    public static User convertStringToUser(String string) {
        String[] split = string.split(",");

        if (split.length != 3) {
            throw new IllegalArgumentException("Количество разделителей в считываемом файле неверное");
        }

        // id,name,password,role

        return User.builder()
                .id(UUID.fromString(split[0]))
                .name(split[1])
                .password(split[2])
                .role(Role.valueOf(split[3]))
                .build();
    }

}
