package aa.billing.model;

import aa.common.model.Born;
import aa.common.model.HasRole;
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
@Table(name = "billing_accounts")
public class Account extends Born implements HasRole {

    @Column(name = "ext_id", unique = true, nullable = false)
    private long extId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
