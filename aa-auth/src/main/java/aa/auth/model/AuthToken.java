package aa.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "auth_tokens")
public class AuthToken extends Born {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser user;

    @Column(name = "token", unique = true, nullable = false, length = 4096)
    private String token;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

}
