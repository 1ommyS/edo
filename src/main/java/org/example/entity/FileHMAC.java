package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileHMAC {
    private UUID id;
    private String fileName;
    private String hash;

    @Override
    public String toString() {
        return MessageFormat.format("{},{},{}", id, fileName, hash);
    }
}
