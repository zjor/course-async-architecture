package aa.billing.repository;

import aa.billing.model.AuditLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AuditLogRepository extends CrudRepository<AuditLog, Long> {
}
