package aa.auth.controller;

import aa.auth.ext.spring.aop.Log;
import aa.auth.model.AuthUser;
import aa.auth.repository.AuthTokenRepository;
import aa.auth.repository.AuthUserRepository;
import aa.auth.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthUserRepository userRepository;
    private final AuthTokenRepository tokenRepository;

    private final TokenService tokenService;

    public AuthController(
            AuthUserRepository userRepository,
            AuthTokenRepository tokenRepository,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    @Log
    @PostMapping("register")
    public Object register(@RequestBody RegistrationRequest req) {
        var user = userRepository.save(AuthUser.builder()
                .login(req.login())
                .password(req.password())
                .role(req.role())
                .build());
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
    public Object verify() {
        return null;
    }

    @Log
    @PostMapping("logout")
    public void logout() {

    }

    @Log
    @DeleteMapping("delete")
    public void delete() {

    }

    public record RegistrationRequest(String login, String password, AuthUser.Role role) {
    }

    public record RegistrationResponse(long id, String login, AuthUser.Role role, Instant createdAt) {
        public static RegistrationResponse of(AuthUser u) {
            return new RegistrationResponse(u.getId(), u.getLogin(), u.getRole(), u.getCreatedAt());
        }
    }

    public record LoginRequest(String login, String password) {
    }

    public record LoginResponse(String token) {
    }

}
