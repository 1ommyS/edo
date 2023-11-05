package org.example.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author 1ommy
 * @version 01.11.2023
 */

@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PACKAGE) // package-private
public class User {
    UUID id;
    String name;
    String password;

    @Override
    public String toString() {
        return MessageFormat.format("{},{},{}", id, name, password);
    }
}
