package aa.billing.controller;

import aa.billing.model.Account;
import aa.billing.model.AuditLog;
import aa.billing.repository.AuditLogRepository;
import aa.billing.repository.BalanceRepository;
import aa.billing.repository.BillingCycleReportRepository;
import aa.billing.service.BillingService;
import aa.billing.util.TimeUtil;
import aa.common.auth.AuthenticatedUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static aa.billing.config.SwaggerConfiguration.SECURITY_REQUIREMENT_JWT;
import static aa.common.auth.AuthValidator.hasRoles;
import static aa.common.auth.AuthValidator.notOneOf;
import static aa.common.model.Role.ACCOUNTANT;
import static aa.common.model.Role.ADMIN;
import static aa.common.model.Role.MANAGER;

@RestController
@RequestMapping("api/v1/billing")
@SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
public class BillingController {

    private final BillingService billingService;
    private final BalanceRepository balanceRepository;
    private final AuditLogRepository auditLogRepository;
    private final BillingCycleReportRepository billingCycleReportRepository;

    public BillingController(
            BillingService billingService,
            BalanceRepository balanceRepository,
            AuditLogRepository auditLogRepository,
            BillingCycleReportRepository billingCycleReportRepository) {
        this.billingService = billingService;
        this.balanceRepository = balanceRepository;
        this.auditLogRepository = auditLogRepository;
        this.billingCycleReportRepository = billingCycleReportRepository;
    }

    @RequestMapping("admin/stats")
    public GetAdminStatsResponse adminStats(@AuthenticatedUser Account account) {
        if (!hasRoles(account, ADMIN, ACCOUNTANT)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var billingCycleStart = TimeUtil.getCycleStartFromNow();
        return new GetAdminStatsResponse(
                billingService.getEarnings(billingCycleStart, Instant.now()),
                billingCycleReportRepository.findAllOrdered().stream()
                        .map(r -> new BillingCycleBalance(r.getCycleStart(), r.getBalance()))
                        .collect(Collectors.toList())
        );
    }

    @RequestMapping("worker/stats")
    public GetWorkerStatsResponse workerStats(@AuthenticatedUser Account account) {
        if (!notOneOf(account, ADMIN, MANAGER)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var balance = balanceRepository.findByAccount(account)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Balance does not exist"));

        return new GetWorkerStatsResponse(
                balance.getBalance(),
                auditLogRepository.findByAccountOrderByCreatedAtDesc(account)
        );
    }

    public record BillingCycleBalance(Instant billingPeriod, BigDecimal balance) {
    }

    public record GetAdminStatsResponse(BigDecimal earnedToday, List<BillingCycleBalance> stats) {
    }

    public record GetWorkerStatsResponse(BigDecimal balance, List<AuditLog> operations) {
    }

}
