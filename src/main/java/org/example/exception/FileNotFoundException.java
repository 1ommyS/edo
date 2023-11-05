package org.example.exception;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException() {
        super("Такого файла нет");
    }
}
