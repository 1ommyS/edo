package org.example;

import org.example.files.FileSearcher;
import org.example.files.FileSearcherOS;

import java.io.File;
import java.util.Optional;

/**
 * @author 1ommy
 * @version 29.10.2023
 */
public class Main {
    public static void main(String[] args) throws Exception {
        FileSearcher fileSearcher = new FileSearcherOS();

        File rootDirectory = new File("/Users/1ommy/development/IT-park/lessons/edo/src/main/resources/files");

        File[] files = rootDirectory.listFiles();

        for (var f : files) {
            System.out.println(f.getAbsolutePath());
        }
    }
}