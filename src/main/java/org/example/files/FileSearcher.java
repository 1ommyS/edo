package org.example.files;

import java.io.File;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author 1ommy
 * @version 29.10.2023
 */
public interface FileSearcher {
    File[] findFiles(Predicate<? super File> rule, String relativePath);

    Optional<File> findFileByName(String fileName);

    File[] findFilesByNamePattern(String pattern);

    File[] findFilesByNamePart(String partOfFileName);
}

