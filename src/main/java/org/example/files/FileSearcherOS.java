package org.example.files;

import org.example.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author 1ommy
 * @version 29.10.2023
 */
public class FileSearcherOS implements FileSearcher {
    private final String ROOT_DIRECTORY = "/Users/1ommy/development/IT-park/lessons/edo/src/main/resources/files/test";

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

    @Override
    public void printFoldersTree(String path, int catalogTabSize) {
        /*
            1) открываем папочку
            2) проверяем, что это директория
            3) берем список всех дочерних элементов и бежим по нему
            4) если встречаем директорию,рекурсивно вызываем себя же
            5) если встретили файл, его печатаем
         */

        var file = new File(ROOT_DIRECTORY + path);

        if (file.isDirectory()) {
            var childElements = file.listFiles();

            if (childElements == null) {
                System.out.println("Каталог пуст");
                return;
            }

            for (File element : childElements) {

                var delimiter = StringUtils.multiplyString("-", catalogTabSize);

                var pattern = element.isDirectory() ? "%s /%s%n" : "%s %s%n";
                System.out.printf(pattern, delimiter, element.getName());
                if (element.isDirectory()) {

                    printFoldersTree("%s/%s".formatted(path, element.getName()), catalogTabSize + 1);
                }
            }
        }
    }

    @Override
    public void deleteFile(String path) {
        var file = new File(ROOT_DIRECTORY + path);

        boolean delete = file.delete();

        if (delete) {
            System.out.println("Вы успешно удалили файл");
        } else {
            System.out.println("Что-то пошло не так");
        }
    }

    public void deleteCatalog(String path) {
        Path newPath = Path.of(ROOT_DIRECTORY, path);

        try {
            Files.walk(newPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

        } catch (IOException e) {
            System.out.println("Что-то пошло не так при удалении");
        }
    }
}
/*
folder1:
- ffile1.txt
- file2.txt
- file34.txt
- folder2:
- - file1.txt
- - file2.txt
*/
