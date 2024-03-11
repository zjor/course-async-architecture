package aa.billing.service;

import aa.billing.model.AuditLog;
import aa.billing.repository.AccountRepository;
import aa.billing.repository.AuditLogRepository;
import aa.billing.repository.BalanceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public class BillingService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final AuditLogRepository auditLogRepository;

    public BillingService(
            AccountRepository accountRepository,
            BalanceRepository balanceRepository,
            AuditLogRepository auditLogRepository) {
        this.accountRepository = accountRepository;
        this.balanceRepository = balanceRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void handleTaskAssigned(long accountExtId, BigDecimal assignmentFee, long taskId) {
        var account = accountRepository.findByExtId(accountExtId)
                .orElseThrow(() -> new RuntimeException("Account not found for ID: " + accountExtId));
        var balance = balanceRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Balance not found for account: " + account.getId()));
        balance.setBalance(balance.getBalance().subtract(assignmentFee));
        balanceRepository.save(balance);

        auditLogRepository.save(AuditLog.builder()
                .account(account)
                .amount(assignmentFee.negate())
                .reason("Task #" + taskId + " assigned")
                .build());
    }

    @Transactional
    public void handleTaskCompleted(long accountExtId, BigDecimal reward, long taskId) {
        var account = accountRepository.findByExtId(accountExtId)
                .orElseThrow(() -> new RuntimeException("Account not found for ID: " + accountExtId));
        var balance = balanceRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Balance not found for account: " + account.getId()));
        balance.setBalance(balance.getBalance().add(reward));
        balanceRepository.save(balance);

        auditLogRepository.save(AuditLog.builder()
                .account(account)
                .amount(reward)
                .reason("Task #" + taskId + " completed")
                .build());
    }

}
