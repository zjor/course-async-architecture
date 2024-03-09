package aa.common.events.auth.v1;

import aa.common.events.auth.EventType;
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

    public static final String SCHEMA = "auth/v1/account_created";

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
