package org.example.files;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author 1ommy
 * @version 29.10.2023
 */
public class FileSearcherOS implements FileSearcher {
    private final String ROOT_DIRECTORY = "/src/main/resources/files";

    /**
     * @param rule         лямбда-функция,которая будет фильтровать все найденные в этой директории файлы
     * @param relativePath если хотите искать по пути, отличному от ROOT, передавайте аргумент != null.
     * @return File[] массив найденных файлов
     */
    @Override
    public File[] findFiles(Predicate<? super File> rule, String relativePath) {
        File rootDirectory = new File(ROOT_DIRECTORY + (relativePath == null ? "" : relativePath));

        File[] files = rootDirectory.listFiles();

        if (files == null || files.length == 0) return new File[0];

        return Arrays
                .stream(files)
                .filter(file -> !file.isDirectory())
                .filter(rule)
                .toArray(File[]::new);
    }

    @Override
    public Optional<File> findFileByName(String fileName) {

        File[] files = findFiles(
                file -> file.getName().equals(fileName),
                null
        );

        if (files.length == 0) return Optional.empty();

        return Optional.of(files[0]);
    }

    @Override
    public File[] findFilesByNamePattern(String pattern) {
        return findFiles(
                file -> file.getName().matches(pattern),
                null
        );
    }

    @Override
    public File[] findFilesByNamePart(String partOfFileName) {
        return findFiles(
                file -> file.getName().contains(partOfFileName),
                null
        );
    }
}
