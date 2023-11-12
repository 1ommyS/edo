package org.example.service;

import lombok.Getter;
import org.example.configuration.ModelMapperInstance;
import org.example.database.Database;
import org.example.dto.TaskDTO;
import org.example.entity.Task;
import org.example.entity.User;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author 1ommy
 * @version 09.11.2023
 */
public class TaskService {
    private final Database database;
    @Getter
    private List<Task> tasks;
    private final ModelMapper modelMapper = ModelMapperInstance.createInstance();

    public TaskService(Database database) {
        this.database = database;
        tasks = database.readTasks("tasks.csv");
    }

    // dto = data transfer object
    public Task assignTask(TaskDTO dto) {
        // entity = copy(dto) + isCompeleted=false

        /*
        Task build = Task.builder()
                .id(dto.id())
                .description(dto.description())
                .title(dto.title())
                .isCompleted(false)
                .build();
*/
        Task task = modelMapper.map(dto, Task.class);
        tasks.add(task);

        return task;
    }

    public void writeDataToFile() {
        database.writeTasksToFile("tasks.csv", tasks);
    }
    public void createTask(TaskDTO taskDTO) {
        Task map = modelMapper.map(taskDTO, Task.class);
        tasks.add(map);
        writeDataToFile();

    }
    public Task assignTask(String title, String name) {
        Optional<Task> optionalTask = tasks.parallelStream().filter(x -> x.getTitle().equals(title)).findFirst();
        if(optionalTask.isEmpty()) {
            System.out.println("Такой задачи нет");
            return null;
        }
        Task task = optionalTask.get();
        AuthorisationService authorisationService = new AuthorisationService(database);
        Optional<User> optionalUser = authorisationService.getUsers().parallelStream().filter(x -> x.getName().equals(name)).findFirst();
        if ((optionalUser.isEmpty())) {
            System.out.println("Нет такого человека");
            return null;
        }
        task.setSolver_id(optionalUser.get().getId());
        return task;


    }
}
