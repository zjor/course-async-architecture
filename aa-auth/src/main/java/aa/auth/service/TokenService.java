package aa.auth.service;

import aa.auth.model.AuthToken;
import aa.auth.model.AuthUser;
import aa.auth.repository.AuthTokenRepository;
import aa.auth.repository.AuthUserRepository;
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

@Slf4j
public class TokenService {

    private final AuthUserRepository userRepository;
    private final AuthTokenRepository tokenRepository;

    private final SecretKey key;

    public TokenService(
            AuthUserRepository userRepository,
            AuthTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        var in = TokenService.class.getClassLoader().getResourceAsStream("signing.key");
        try (var r = new BufferedReader(new InputStreamReader(in))) {
            key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(r.readLine()));
        } catch (IOException e) {
            log.error("Failed to read key: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public AuthToken issueToken(AuthUser user) {
        var token = Jwts.builder()
                .subject(user.getLogin()).claim("role", user.getRole())
                .signWith(key).compact();
        return tokenRepository.save(AuthToken.builder()
                        .user(user)
                        .token(token)
                .build());
    }

    public void expireAll(AuthUser user) {

    }

    public Jws<Claims> verify(AuthToken token) {
        return null;
    }

}
