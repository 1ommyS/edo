package org.example.mappers;

import org.example.entity.FileHMAC;

import java.util.UUID;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
public class FileHMACMapper {
    public static FileHMAC convertStringToFileHMAC(String string) {
        String[] split = string.split(",");
        if (split.length != 3) {
            throw new IllegalArgumentException("Количество разделителей в считываемом файле неверное");
        }

        return FileHMAC.builder()
                .id(UUID.fromString(split[0]))
                .fileName(split[1])
                .hash(split[2])
                .build();
    }
}
