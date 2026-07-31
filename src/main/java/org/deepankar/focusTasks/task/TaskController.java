package org.deepankar.focusTasks.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());

        return taskRepository.save(task);
    }

    @GetMapping
    public List<Task> getTasks(
            @RequestParam(required = false) Boolean completed
    ) {
        if (completed == null) {
            return taskRepository.findAll();
        }

        return taskRepository.findByCompleted(completed);
    }

    @PatchMapping("/{id}/complete")
    public Task completeTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.complete();
        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found");
        }

        taskRepository.deleteById(id);
    }

    public record CreateTaskRequest(
            @NotBlank(message = "Title is required")
            @Size(max = 200, message = "Title must be at most 200 characters")
            String title
    ) {
    }

    public record UpdateTaskStatusRequest(boolean completed) {
    }

    @PatchMapping("/{id}")
    public Task updateTaskStatus(
            @PathVariable Long id,
            @RequestBody UpdateTaskStatusRequest request
    ) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setCompleted(request.completed());
        return taskRepository.save(task);
    }
}