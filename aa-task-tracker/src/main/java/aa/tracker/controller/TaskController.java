package aa.tracker.controller;

import aa.common.ext.spring.aop.Log;
import aa.common.model.Role;
import aa.tracker.auth.AuthenticatedUser;
import aa.tracker.model.Account;
import aa.tracker.model.Task;
import aa.tracker.repository.TaskRepository;
import aa.tracker.service.TaskAssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static aa.tracker.config.SwaggerConfiguration.SECURITY_REQUIREMENT_JWT;

@RestController
@RequestMapping("api/v1/tasks")
@SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
public class TaskController {

    private final TaskAssignmentService assignmentService;
    private final TaskRepository taskRepository;
    private final Random random;

    public TaskController(
            TaskAssignmentService assignmentService,
            TaskRepository taskRepository) {
        this.assignmentService = assignmentService;
        this.taskRepository = taskRepository;
        random = new Random(42);
    }

    @Log
    @PostMapping("create")
    public TaskDTO create(@AuthenticatedUser Account account, @RequestBody CreateTaskRequest req) {
        var assignee = assignmentService.getAssignee()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No suitable assignees"));
        var task = taskRepository.save(Task.builder()
                .assignee(assignee)
                .description(req.description())
                .reward(BigDecimal.valueOf(random.nextDouble(20, 40)))
                .assignmentFee(BigDecimal.valueOf(random.nextDouble(10, 20)))
                .build());
        //TODO: send event to deduct assignmentFee
        return TaskDTO.of(task);
    }

    @Log
    @GetMapping
    public List<TaskDTO> list(@AuthenticatedUser Account account) {
        return taskRepository.findAllByAssigneeOrderByCreatedAtDesc(account)
                .stream().map(TaskDTO::of).collect(Collectors.toList());
    }

    @Log
    @PostMapping("reassign")
    public void reassign(@AuthenticatedUser Account account) {
        if (!account.getRole().equals(Role.ADMIN) &&
                !account.getRole().equals(Role.MANAGER)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not an admin or manager");
        }
        taskRepository.findByDoneOrderByCreatedAtDesc(false)
                .forEach(task -> {
                    var assignee = assignmentService.getAssignee()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No suitable assignees"));
                    task.setAssignee(assignee);
                    taskRepository.save(task);
                    //TODO: send event to deduct assignmentFee
                });

    }

    @PutMapping("{id}/set-done")
    public TaskDTO setDone(
            @AuthenticatedUser Account account,
            @PathVariable("id") Long id,
            @RequestBody SetDoneRequest req) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.valueOf(id)));
        if (!task.getAssignee().equals(account)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not an assignee");
        }
        task.setDone(req.status());
        //TODO: send event to accrue rewards
        return TaskDTO.of(taskRepository.save(task));
    }

    public record CreateTaskRequest(String description) {
    }

    public record TaskDTO(long id, Account assignee, String description, BigDecimal reward,
                          BigDecimal assignmentFee, boolean done, Instant createdAt) {
        public static TaskDTO of(Task task) {
            return new TaskDTO(
                    task.getId(),
                    task.getAssignee(),
                    task.getDescription(),
                    task.getReward(),
                    task.getAssignmentFee(),
                    task.isDone(),
                    task.getCreatedAt());
        }
    }

    public record SetDoneRequest(boolean status) {
    }

}
