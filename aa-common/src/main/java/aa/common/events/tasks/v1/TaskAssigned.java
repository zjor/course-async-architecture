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
public class TaskAssigned {

    private static final String NAME = "task_assigned";
    private static final String PRODUCER = "tasks";
    private static final int VERSION = 1;

    public static final String SCHEMA = PRODUCER + "/v" + VERSION + "/" + NAME;

    @JsonProperty("task_id")
    private long taskId;

    @JsonProperty("assignee_id")
    private long assigneeId;

    @JsonProperty("assignment_fee")
    private BigDecimal assignmentFee;

    public Event<TaskAssigned> toEvent() {
        return Event.<TaskAssigned>builder()
                .id(UUID.randomUUID().toString())
                .name(NAME)
                .version(VERSION)
                .producer(PRODUCER)
                .data(this)
                .build();
    }

}
