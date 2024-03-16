package aa.billing.controller;

import aa.billing.model.Account;
import aa.billing.service.BillingService;
import aa.billing.util.TimeUtil;
import aa.common.auth.AuthenticatedUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static aa.billing.config.SwaggerConfiguration.SECURITY_REQUIREMENT_JWT;
import static aa.common.auth.AuthValidator.hasRoles;
import static aa.common.model.Role.ADMIN;

@RestController
@RequestMapping("api/v1/analytics")
@SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
public class AnalyticsController {

    private final BillingService billingService;

    public AnalyticsController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public GetAnalyticsResponse getAnalytics(@AuthenticatedUser Account account) {
        if (!hasRoles(account, ADMIN)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        var billingCycleStart = TimeUtil.floorNowTo(5 * 60L);
        return new GetAnalyticsResponse(
                billingService.getEarnings(billingCycleStart, Instant.now()),
                0,
                List.of()
        );


    }

    public record MostExpensiveTask(Instant billingPeriod, BigDecimal price) {
    }

    public record GetAnalyticsResponse(
            BigDecimal earnedToday,
            int negativeBalancesCount,
            List<MostExpensiveTask> mostExpensiveTasks) {
    }

}
