package aa.billing.repository;

import aa.billing.model.AuditLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
@Transactional
public interface AuditLogRepository extends CrudRepository<AuditLog, Long> {

    @Query("""
            SELECT a FROM AuditLog a 
            WHERE a.createdAt >= :from AND a.createdAt <= :to
            """)
    List<AuditLog> findBetweenTimestamps(Instant from, Instant to);

}
