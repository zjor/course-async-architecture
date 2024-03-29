package aa.auth.controller;

import aa.auth.ext.spring.auth.AuthFilter;
import aa.auth.ext.spring.auth.AuthenticatedUser;
import aa.auth.model.AuthToken;
import aa.auth.model.AuthUser;
import aa.auth.service.KafkaService;
import aa.common.model.Role;
import aa.auth.repository.AuthUserRepository;
import aa.auth.service.AuthTokenService;
import aa.common.ext.spring.aop.Log;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static aa.auth.config.SwaggerConfiguration.SECURITY_REQUIREMENT_JWT;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthUserRepository userRepository;
    private final AuthTokenService tokenService;
    private final KafkaService kafkaService;

    public AuthController(
            AuthUserRepository userRepository,
            AuthTokenService tokenService,
            KafkaService kafkaService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.kafkaService = kafkaService;
    }

    @Log
    @PostMapping("register")
    public Object register(@RequestBody RegistrationRequest req) {
        var user = userRepository.save(AuthUser.builder()
                .login(req.login())
                .password(req.password())
                .role(req.role())
                .build());
        kafkaService.sendAccountCreatedEventAsync(user);
        return RegistrationResponse.of(user);
    }

    @Log
    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        var userOpt = userRepository.findByLogin(req.login());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist: " + req.login());
        }
        var user = userOpt.get();
        if (!user.getPassword().equals(req.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password for login: " + req.login());
        }
        var token = tokenService.issueToken(user);
        return new LoginResponse(token.getToken());
    }

    @Log
    @GetMapping("verify")
    @SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
    public RegistrationResponse verify(@AuthenticatedUser AuthUser user, HttpServletRequest req) {
        var token = (AuthToken) req.getAttribute(AuthFilter.AUTH_TOKEN_ATTRIBUTE);
        if (token.getExpiredAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired");
        }
        try {
            tokenService.verify(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Verification failed: " + e.getMessage());
        }
        return RegistrationResponse.of(user);
    }

    @Log
    @PostMapping("logout")
    @SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
    public void logout(@AuthenticatedUser AuthUser user) {
        tokenService.expireAll(user);
    }

    @Log
    @DeleteMapping("delete")
    @SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
    public void delete(@AuthenticatedUser AuthUser user) {
        logout(user);
        user.setDeletedAt(Instant.now());
        userRepository.save(user);
        kafkaService.sendAccountDeletedEventAsync(user);
    }

    @PutMapping("role")
    @SecurityRequirements({@SecurityRequirement(name = SECURITY_REQUIREMENT_JWT)})
    public ChangeRoleResponse changeRole(
            @AuthenticatedUser AuthUser user,
            @RequestBody ChangeRoleRequest req
    ) {
        user.setRole(req.role());
        userRepository.save(user);
        kafkaService.setAccountRoleChangedEventAsync(user);
        return ChangeRoleResponse.of(user);
    }

    public record RegistrationRequest(String login, String password, Role role) {
    }

    public record RegistrationResponse(long id, String login, Role role, Instant createdAt) {
        public static RegistrationResponse of(AuthUser u) {
            return new RegistrationResponse(u.getId(), u.getLogin(), u.getRole(), u.getCreatedAt());
        }
    }

    public record LoginRequest(String login, String password) {
    }

    public record LoginResponse(String token) {
    }

    public record ChangeRoleRequest(Role role) {
    }

    public record ChangeRoleResponse(long id, String login, Role role, Instant createdAt) {
        public static ChangeRoleResponse of(AuthUser u) {
            return new ChangeRoleResponse(u.getId(), u.getLogin(), u.getRole(), u.getCreatedAt());
        }
    }

}
