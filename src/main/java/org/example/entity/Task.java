package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author 1ommy
 * @version 09.11.2023
 */

@Data
@AllArgsConstructor
@Builder
public class Task implements Serializable {
    private UUID id;
    private String title;
    private String description;
    private UUID solver_id;
    private boolean isCompleted;

    @Override
    public String toString() {
        return MessageFormat.format("{},{},{},{},{}", id, title, description, solver_id, isCompleted);
    }
}
