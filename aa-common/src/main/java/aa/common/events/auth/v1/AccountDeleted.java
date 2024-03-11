package aa.common.events.auth.v1;

import aa.common.events.Event;
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
public class AccountDeleted {

    public static final String SCHEMA = "auth/v1/account_deleted";

    @JsonProperty("id")
    private long id;

    @JsonProperty("deleted_at")
    private long deletedAt;

    public Event<AccountDeleted> toEvent() {
        return Event.<AccountDeleted>builder()
                .id(UUID.randomUUID().toString())
                .name("account_deleted")
                .version(1)
                .producer("auth")
                .data(this)
                .build();
    }

}
