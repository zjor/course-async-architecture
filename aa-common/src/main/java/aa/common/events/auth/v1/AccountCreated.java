package aa.common.events.auth.v1;

import aa.common.events.Event;
import aa.common.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreated {

    public static final String SCHEMA = "auth/v1/account_created";

    @JsonProperty("id")
    private long id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("created_at")
    private long createdAt;

    public Event<AccountCreated> toEvent() {
        return Event.<AccountCreated>builder()
                .id(UUID.randomUUID().toString())
                .name("account_created")
                .version(1)
                .producer("auth")
                .data(this)
                .build();
    }

}
