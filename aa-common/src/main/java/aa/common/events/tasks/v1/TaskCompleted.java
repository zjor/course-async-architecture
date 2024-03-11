package aa.common.events.tasks.v1;

import aa.common.events.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompleted {

    private static final String NAME = "task_completed";
    private static final String PRODUCER = "tasks";
    private static final int VERSION = 1;

    public static final String SCHEMA = PRODUCER + "/v" + VERSION + "/" + NAME;

    @JsonProperty("task_id")
    private long taskId;

    @JsonProperty("assignee_id")
    private long assigneeId;

    @JsonProperty("reward")
    private BigDecimal reward;

    public Event<TaskCompleted> toEvent() {
        return Event.<TaskCompleted>builder()
                .id(UUID.randomUUID().toString())
                .name(NAME)
                .version(VERSION)
                .producer(PRODUCER)
                .data(this)
                .build();
    }

}
