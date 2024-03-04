package aa.tracker.model;

import aa.common.model.Born;
import aa.common.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tm_accounts")
public class Account extends Born {
    private long extId;
    private String login;
    private Role role;
    private Instant deletedAt;
}
