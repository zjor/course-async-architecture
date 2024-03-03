package aa.common.events.auth;

import aa.common.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleChanged {

    @Builder.Default
    @JsonProperty("type")
    private EventType type = EventType.ACCOUNT_ROLE_CHANGED;

    @JsonProperty("id")
    private long id;

    @JsonProperty("role")
    private Role role;

}
