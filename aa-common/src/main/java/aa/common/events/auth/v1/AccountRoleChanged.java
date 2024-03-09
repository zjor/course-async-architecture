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
public class AccountRoleChanged {

    private static final String NAME = "account_role_changed";
    private static final String PRODUCER = "auth";
    private static final int VERSION = 1;

    public static final String SCHEMA = PRODUCER + "/v" + VERSION + "/" + NAME;

    @JsonProperty("id")
    private long id;

    @JsonProperty("role")
    private Role role;

    public Event<AccountRoleChanged> toEvent() {
        return Event.<AccountRoleChanged>builder()
                .id(UUID.randomUUID().toString())
                .name(NAME)
                .version(VERSION)
                .producer(PRODUCER)
                .data(this)
                .build();
    }


}
