package org.example.mappers;

import org.example.entity.Task;

import java.util.UUID;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
public class TaskMapper {
    public static Task convertStringToTask(String string) {
        String[] split = string.split(",");

        if (split.length != 5) {
            throw new IllegalArgumentException("Количество разделителей в считываемом файле неверное");
        }

        // id,title,description,solver_id

        return Task.builder()
                .id(UUID.fromString(split[0]))
                .title(split[1])
                .description(split[2])
                .solver_id(UUID.fromString(split[3]))
                .isCompleted(Boolean.valueOf(split[4]))
                .build();
    }


}
