package aa.billing.service;

import aa.billing.model.AuditLog;
import aa.billing.repository.AuditLogRepository;
import aa.billing.repository.BalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;

@Slf4j
public class PayoutService {

    private final BalanceRepository balanceRepository;
    private final AuditLogRepository auditLogRepository;

    public PayoutService(BalanceRepository balanceRepository, AuditLogRepository auditLogRepository) {
        this.balanceRepository = balanceRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void payout() {
        log.info("Payout started");
        balanceRepository.findAll().forEach(b -> {
            if (b.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.info("Paying to {} amount: {}", b.getAccount().getExtId(), b.getBalance());
                auditLogRepository.save(AuditLog.builder()
                                .account(b.getAccount())
                                .amount(b.getBalance().negate())
                                .reason("Payout")
                        .build());
                b.setBalance(BigDecimal.ZERO);
                balanceRepository.save(b);
                //TODO: send email
                //TODO: send PayoutCompleted event
            }
        });
    }

}
