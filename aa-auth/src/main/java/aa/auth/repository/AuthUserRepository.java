package aa.auth.repository;

import aa.auth.model.AuthUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface AuthUserRepository extends CrudRepository<AuthUser, Long> {

    Optional<AuthUser> findByLogin(String login);

}
