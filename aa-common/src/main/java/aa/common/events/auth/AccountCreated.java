package aa.common.events.auth;

import aa.common.model.Role;
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
public class AccountCreated {

    @Builder.Default
    @JsonProperty("type")
    private EventType type = EventType.ACCOUNT_CREATED;

    @JsonProperty("id")
    private long id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("created_at")
    private Instant createdAt;

}
