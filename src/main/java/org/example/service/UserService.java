package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Task;
import org.example.entity.User;

import java.util.List;
import java.util.Objects;

/**
 * @author 1ommy
 * @version 09.11.2023
 */
@RequiredArgsConstructor
public class UserService {
    private final TaskService service;

    public void displayMyTasks(User authenticatedUser) {
        List<Task> tasks = service.getTasks();
      /*  for (var task : tasks) {
          *//*  if (task.getSolver_id().equals(authenticatedUser.getId())) {
                System.out.println(task);
            }*//*
            if (Objects.equals(task.getSolver_id(), authenticatedUser.getId())) {
                System.out.println(task);
            }
        }
*/
        tasks.parallelStream()
                .filter(task -> Objects.equals(task.getSolver_id(), authenticatedUser.getId()))
                .forEach(System.out::println);
    }

    public void displayMyNotCompletedTasks(User authenticatedUser) {
        List<Task> tasks = service.getTasks();

        tasks.parallelStream()
                .filter(task -> Objects.equals(task.getSolver_id(), authenticatedUser.getId()) && !task.isCompleted())
                .forEach(System.out::println);
    }
    public void displayMyCompletedTasks(User authenticatedUser) {
        List<Task> tasks = service.getTasks();

        tasks.parallelStream()
                .filter(task -> Objects.equals(task.getSolver_id(), authenticatedUser.getId()) && task.isCompleted())
                .forEach(System.out::println);
    }
}
