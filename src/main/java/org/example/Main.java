package org.example;

import lombok.val;
import org.example.files.FileSearcher;
import org.example.files.FileSearcherOS;

import java.io.File;

/**
 * @author 1ommy
 * @version 29.10.2023
 */
public class Main {
    public static void main(String[] args) throws Exception {
        FileSearcher fileSearcher = new FileSearcherOS();

        File rootDirectory = new File("/Users/1ommy/development/IT-park/lessons/edo/src/main/resources/files/test");

        fileSearcher.printFoldersTree("", 1);
        fileSearcher.deleteCatalog("/folder2");

        val puk2 = "Helloe";
        final var puk = "Hello";

        User user = User.builder()
                .weight(10)
                .piskaSize(20)
                .siskaSize(5)
                .build();

        System.out.println(user);

//        File[] files = rootDirectory.listFiles();

//        for (var f : files) {
//            System.out.println(f.getPath());
//        }
    }
}