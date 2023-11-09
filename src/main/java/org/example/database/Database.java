package org.example.database;

import lombok.Data;
import org.example.entity.FileHMAC;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.mappers.FileHMACMapper;
import org.example.mappers.TaskMapper;
import org.example.mappers.UserMapper;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 1ommy
 * @version 05.11.2023
 */
@Data
public class Database {

    private final String basePath = "src/resources/files";

    public void createTable(String title, String... columns) throws IOException {
        var file = Path.of(basePath, title + ".csv").toFile();

        String firstRow = String.join(",", columns);

        if (file.exists()) throw new FileAlreadyExistsException("Такой файл уже есть");

        file.createNewFile();

        insertIntoTable(title, firstRow);
    }

    public List<User> readUsers(String filename) {
        List<User> res = new ArrayList<>();
        String line;
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();
            while ((line = br.readLine()) != null) {
                res.add(UserMapper.convertStringToUser(line));
            }
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException("Файл не существует либо ошибка чтения", e);
        }
        return res;
    }

    public List<Task> readTasks(String filename) {
        List<Task> res = new ArrayList<>();
        String line;
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();
            while ((line = br.readLine()) != null) {
                res.add(TaskMapper.convertStringToTask(line));
            }
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException("Файл не существует либо ошибка чтения", e);
        }
        return res;
    }


    public List<FileHMAC> readFilesHMACs(String filename) {
        List<FileHMAC> res = new ArrayList<>();
        String line;
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();
            while ((line = br.readLine()) != null) {
                res.add(FileHMACMapper.convertStringToFileHMAC(line));
            }
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException("Файл не существует либо ошибка чтения", e);
        }
        return res;
    }


    public void insertIntoTable(String table, String data) {
        try {
            FileOutputStream fileOut = new FileOutputStream(Path.of(basePath, table + ".csv").toString());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(data);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeTasksToFile(String filename, List<Task> tasks) {
        List<Task> res = new ArrayList<>();
        String line;
        FileWriter fr;
        BufferedWriter br;
        try {
            fr = new FileWriter(filename);
            br = new BufferedWriter(fr);
            for (Task task : tasks) {
                br.write(task.toString()+"\n");
            }
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException("Файл не существует либо ошибка чтения", e);
        }
    }
}
