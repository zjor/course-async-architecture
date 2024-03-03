package aa.tracker;

import aa.common.ext.spring.aop.Log;
import aa.tracker.auth.AuthenticatedUser;
import aa.tracker.model.Account;
import aa.tracker.model.Task;
import aa.tracker.repository.TaskRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

import static aa.tracker.config.SwaggerConfiguration.SECURITY_REQUIREMENT_JWT;

@RestController
@RequestMapping("api/v1/tasks")
@SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Log
    @PostMapping("create")
    public CreateTaskResponse create(@AuthenticatedUser Account account, @RequestBody CreateTaskRequest req) {
        var task = taskRepository.save(Task.builder()
                        .assignee(account) //TODO: fix
                        .description(req.description())
                        .price(BigDecimal.TEN) //TODO: fix
                .build());
        return new CreateTaskResponse(
                task.getId(),
                task.getAssignee(),
                task.getDescription(),
                task.getPrice(),
                task.isDone(),
                task.getCreatedAt());
    }

    public void list() {

    }

    public void reassign() {

    }

    public void setDone() {

    }

    public record CreateTaskRequest(String description) {
    }

    public record CreateTaskResponse(long id, Account assignee, String description, BigDecimal price, boolean done, Instant createdAt) {}

}
