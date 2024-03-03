package aa.auth.repository;

import aa.auth.model.AuthToken;
import aa.auth.model.AuthUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
@Transactional
public interface AuthTokenRepository extends CrudRepository<AuthToken, Long> {

    @Query("""
            SELECT t 
            FROM AuthToken t, AuthUser u 
            WHERE 
                t.user = u AND 
                t.expiredAt IS NULL 
            ORDER BY t.createdAt 
            LIMIT 1""")
    Optional<AuthToken> findActive(AuthUser user);

    default void expire(AuthToken t) {
        t.setExpiredAt(Instant.now());
        save(t);
    }

}
