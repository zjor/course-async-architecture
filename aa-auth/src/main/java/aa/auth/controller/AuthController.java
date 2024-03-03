package aa.auth.controller;

import aa.auth.ext.spring.aop.Log;
import aa.auth.model.AuthUser;
import aa.auth.repository.AuthUserRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthUserRepository authUserRepository;

    public AuthController(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Log
    @PostMapping("register")
    public Object register(@RequestBody RegistrationRequest req) {
        var user = authUserRepository.save(AuthUser.builder()
                .login(req.login())
                .password(req.password())
                .role(req.role())
                .build());
        return RegistrationResponse.of(user);
    }

    @Log
    @PostMapping("login")
    public Object login() {
        return null;
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

}
