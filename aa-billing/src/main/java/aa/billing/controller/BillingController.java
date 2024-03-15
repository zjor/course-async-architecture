package aa.billing.controller;

import aa.billing.model.Account;
import aa.common.auth.AuthenticatedUser;
import aa.common.model.Role;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static aa.billing.config.SwaggerConfiguration.SECURITY_REQUIREMENT_JWT;

@RestController
@RequestMapping("api/v1/billing")
@SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
public class BillingController {

    @RequestMapping("admin/stats")
    public Object adminStats(@AuthenticatedUser Account account) {
        if (!account.getRole().equals(Role.ADMIN) &&
                !account.getRole().equals(Role.ACCOUNTANT)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return null;
    }

    @RequestMapping("worker/stats")
    public Object workerStats() {
        return null;
    }

}
