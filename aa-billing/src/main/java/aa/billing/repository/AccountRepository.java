package aa.billing.repository;

import aa.billing.model.Account;
import aa.common.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
@Transactional
public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByExtId(long extId);

    default Account ensure(long extId, String login, Role role) {
        var found = findByExtId(extId);
        if (found.isEmpty()) {
            return save(Account.builder()
                    .extId(extId)
                    .role(role)
                    .build());
        } else {
            return found.get();
        }
    }

    default Account setRole(long extId, Role role) {
        var found = findByExtId(extId);
        return found.map(a -> {
            a.setRole(role);
            return save(a);
        }).orElseThrow(() -> new RuntimeException("Account not found. ID: " + extId));
    }

    default Account setDeletedAt(long extId, long deletedAt) {
        var found = findByExtId(extId);
        return found.map(a -> {
            a.setDeletedAt(Instant.ofEpochMilli(deletedAt));
            return save(a);
        }).orElseThrow(() -> new RuntimeException("Account not found. ID: " + extId));
    }

}
