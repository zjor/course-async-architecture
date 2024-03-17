package aa.billing.service;

import aa.billing.model.AuditLog;
import aa.billing.model.BillingCycleReport;
import aa.billing.repository.AuditLogRepository;
import aa.billing.repository.BalanceRepository;
import aa.billing.repository.BillingCycleReportRepository;
import aa.billing.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
public class PayoutService {

    private final BillingService billingService;
    private final BalanceRepository balanceRepository;
    private final AuditLogRepository auditLogRepository;
    private final BillingCycleReportRepository billingCycleReportRepository;

    public PayoutService(
            BillingService billingService,
            BalanceRepository balanceRepository,
            AuditLogRepository auditLogRepository,
            BillingCycleReportRepository billingCycleReportRepository) {
        this.billingService = billingService;
        this.balanceRepository = balanceRepository;
        this.auditLogRepository = auditLogRepository;
        this.billingCycleReportRepository = billingCycleReportRepository;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void payout() {
        var cycleStart = TimeUtil.getCycleStartFrom(Instant.ofEpochMilli(System.currentTimeMillis() - 60_000L));
        var cycleEnd = TimeUtil.getCycleEnd(cycleStart);
        log.info("Payout started for cycle: {} - {}", cycleStart, cycleEnd);
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
        storeBillingCycleReport(cycleStart, cycleEnd);
    }

    // TODO: this should be in a separate analytics service
    private void storeBillingCycleReport(Instant start, Instant end) {

        billingCycleReportRepository.save(
                new BillingCycleReport(start,
                        billingService.getEarnings(start, end),
                        billingService.getMostExpensiveTask(start, end))
        );
    }
}
