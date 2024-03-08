package aa.tracker.service;

import aa.common.model.Role;
import aa.tracker.model.Account;
import aa.tracker.repository.AccountRepository;
import org.apache.commons.lang3.stream.Streams;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskAssignmentService {

    private final AccountRepository accountRepository;

    public TaskAssignmentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Returns random user except Admin or Manager role
     *
     * @return
     */
    public Optional<Account> getAssignee() {
        var candidates = Streams.of(accountRepository.findAll())
                .filter(a -> !(a.getRole() == Role.ADMIN || a.getRole() == Role.MANAGER))
                .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            return Optional.empty();
        } else {
            Collections.shuffle(candidates);
            return Optional.of(candidates.get(0));
        }
    }

}
