package aa.billing.repository;

import aa.billing.model.Account;
import aa.billing.model.Balance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface BalanceRepository extends CrudRepository<Balance, Long> {

    Optional<Balance> findByAccount(Account account);

}
