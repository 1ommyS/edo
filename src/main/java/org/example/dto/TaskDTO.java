package org.example.dto;

import lombok.Builder;

import java.util.UUID;

/**
 * @author 1ommy
 * @version 09.11.2023
 */

@Builder
public record TaskDTO(
        UUID id,
        String title,
        String description,
        UUID solver_id
) {
}

