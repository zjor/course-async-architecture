package aa.auth.service;

import aa.auth.model.AuthToken;
import aa.auth.model.AuthUser;
import aa.auth.repository.AuthTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
public class AuthTokenService {

    private final AuthTokenRepository tokenRepository;

    private final SecretKey key;

    public AuthTokenService(AuthTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        var in = AuthTokenService.class.getClassLoader().getResourceAsStream("signing.key");
        try (var r = new BufferedReader(new InputStreamReader(in))) {
            key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(r.readLine()));
        } catch (IOException e) {
            log.error("Failed to read key: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public AuthToken issueToken(AuthUser user) {
        var token = Jwts.builder()
                .subject(user.getLogin())
                .issuedAt(new Date())
                .claim("role", user.getRole())
                .signWith(key).compact();
        return tokenRepository.save(AuthToken.builder()
                        .user(user)
                        .token(token)
                        .expiredAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build());
    }

    public void expireAll(AuthUser user) {
        var now = Instant.now();
        tokenRepository.findAllActive(user, now).forEach(t -> {
            t.setExpiredAt(now);
            tokenRepository.save(t);
        });
    }

    public Jws<Claims> verify(AuthToken token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token.getToken());
    }

}
