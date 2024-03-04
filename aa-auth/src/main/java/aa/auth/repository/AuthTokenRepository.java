package aa.auth.repository;

import aa.auth.model.AuthToken;
import aa.auth.model.AuthUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AuthTokenRepository extends CrudRepository<AuthToken, Long> {

    Optional<AuthToken> findByToken(String token);

    @Query("""
            SELECT t 
            FROM AuthToken t 
            WHERE 
                t.user = :user AND
                t.expiredAt > :now 
            ORDER BY t.createdAt
            """)
    List<AuthToken> findAllActive(AuthUser user, Instant now);

}
