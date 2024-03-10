package aa.tracker.model;

import aa.common.model.Born;
import aa.common.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "ext_id", unique = true, nullable = false)
    private long extId;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
