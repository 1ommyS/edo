package org.example.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.enums.Role;

import java.util.UUID;

/**
 * @author 1ommy
 * @version 01.11.2023
 */

@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE) // package-private
public class User {
    UUID id;
    String name;
    String password;
    Role role;

    @Override
    public String toString() {
        System.out.println(id + "," + name + "," + password + "," + role);
        return id + "," + name + "," + password + "," + role;
    }
}
