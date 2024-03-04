package aa.common.api;

import aa.common.model.Role;

import java.time.Instant;

public class AuthApi {

    public record RegistrationRequest(String login, String password, Role role) {
    }

    public record RegistrationResponse(long id, String login, Role role, Instant createdAt) {
    }

    public record LoginRequest(String login, String password) {
    }

    public record LoginResponse(String token) {
    }

    public record ChangeRoleRequest(Role role) {
    }

    public record ChangeRoleResponse(long id, String login, Role role, Instant createdAt) {
    }
}
