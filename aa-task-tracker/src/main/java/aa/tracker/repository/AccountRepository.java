package aa.tracker.repository;

import aa.common.model.Role;
import aa.tracker.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                    .login(login)
                    .role(role)
                    .build());
        } else {
            return found.get();
        }
    }

}
