package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacUtils;
import org.example.database.Database;
import org.example.entity.FileHMAC;
import org.example.exception.FileNotFoundException;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.codec.digest.HmacAlgorithms.HMAC_SHA_224;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
@RequiredArgsConstructor
public class SignFileService {
    private final String secretKey = "victoriasecretkey";
    private final String basePath = "src/resources/files";
    private final String table = "fileHMAC";
    private final Database database;

    public void signFile(String fileName, String keyWord) {
        /*
        1) хэшируем кейворд
        2) присваем его конкретному файлу
        3) делаем запись в таблицу связывающую файл и кейворд
         */

        String hmacForFile = new HmacUtils(HMAC_SHA_224, secretKey.getBytes()).hmacHex(keyWord);

        FileHMAC fileHMAC = FileHMAC.builder()
                .id(UUID.randomUUID())
                .fileName(Path.of(basePath, fileName + ".csv").toString())
                .hash(hmacForFile)
                .build();

        database.insertIntoTable(table, fileHMAC.toString());
    }

    public boolean tryToAccessFile(String fileName, String keyWord) {
        List<FileHMAC> files = database.readFilesHMACs(fileName);

        FileHMAC hmac = files.parallelStream()
                .filter(fileHMAC -> fileHMAC.getFileName().equals(fileName))
                .findFirst()
                .orElseThrow(FileNotFoundException::new);

        String hashedKeyword = new HmacUtils(HMAC_SHA_224, secretKey.getBytes()).hmacHex(keyWord);

        return hmac.getHash().equals(hashedKeyword);
    }
}
