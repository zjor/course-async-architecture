package aa.billing.service;

import aa.billing.model.Account;
import aa.billing.model.Balance;
import aa.billing.repository.AccountRepository;
import aa.billing.repository.BalanceRepository;
import aa.common.model.Role;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class AccountService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    public AccountService(AccountRepository accountRepository, BalanceRepository balanceRepository) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public Account ensure(long extId, Role role) {
        return accountRepository.findByExtId(extId).orElseGet(() -> {
            var account = accountRepository.save(Account.builder()
                    .extId(extId)
                    .role(role)
                    .build());
            balanceRepository.save(Balance.builder()
                    .account(account)
                    .balance(BigDecimal.ZERO)
                    .build());
            return account;
        });
    }
}
