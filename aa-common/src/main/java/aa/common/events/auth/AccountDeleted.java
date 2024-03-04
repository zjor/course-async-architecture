package aa.common.events.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDeleted {

    @Builder.Default
    @JsonProperty("type")
    private EventType type = EventType.ACCOUNT_DELETED;

    @JsonProperty("id")
    private long id;

    @JsonProperty("deleted_at")
    private Instant deletedAt;
}
