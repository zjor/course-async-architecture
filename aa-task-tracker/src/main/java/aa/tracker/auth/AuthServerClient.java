package aa.tracker.auth;

import aa.common.api.AuthApi;
import aa.common.ext.spring.aop.Log;
import aa.common.util.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class AuthServerClient {

    private final String baseUrl;
    private final HttpClient httpClient;

    public AuthServerClient(String baseUrl) {
        this.baseUrl = baseUrl;
        httpClient = HttpClient.newBuilder().build();
    }

    @Log
    public AuthApi.RegistrationResponse verify(String token) {
        log.info("Verifying token: {}", token);
        var req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/auth/verify"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .GET().build();
        try {
            var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return JSON.fromJson(res.body(), AuthApi.RegistrationResponse.class);
        } catch (Exception e) {
            log.error("Failed to verify JWT token: " + e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
