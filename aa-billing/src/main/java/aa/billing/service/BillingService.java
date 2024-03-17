package aa.billing.service;

import aa.billing.model.AuditLog;
import aa.billing.repository.AccountRepository;
import aa.billing.repository.AuditLogRepository;
import aa.billing.repository.BalanceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

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

    public BigDecimal getEarnings(Instant from, Instant to) {
        BigDecimal fees = BigDecimal.ZERO;
        BigDecimal rewards = BigDecimal.ZERO;
        for (AuditLog l : auditLogRepository.findBetweenTimestamps(from, to)) {
            if (l.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                fees = fees.add(l.getAmount().negate());
            } else {
                rewards = rewards.add(l.getAmount());
            }
        }
        return fees.subtract(rewards);
    }

    public BigDecimal getMostExpensiveTask(Instant from, Instant to) {
        BigDecimal max = BigDecimal.ZERO;
        for (AuditLog l : auditLogRepository.findBetweenTimestamps(from, to)) {
            if (l.getAmount().compareTo(max) > 0) {
                max = l.getAmount();
            }
        }
        return max;
    }

    public long getNegativeBalancesCount() {
        return balanceRepository.getNegativeBalancesCount();
    }

}
