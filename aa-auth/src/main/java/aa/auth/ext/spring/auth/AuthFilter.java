package aa.auth.ext.spring.auth;

import aa.auth.repository.AuthTokenRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@Slf4j
public class AuthFilter implements Filter {

    public static final String AUTH_USER_ATTRIBUTE = "auth_user";
    public static final String AUTH_TOKEN_ATTRIBUTE = "auth_token";
    public static final String BEARER_PREFIX = "Bearer ";

    private final AuthTokenRepository tokenRepository;

    public AuthFilter(AuthTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        String auth = httpReq.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null) {
            if (auth.toLowerCase().startsWith(BEARER_PREFIX.toLowerCase())) {
                auth = auth.substring(BEARER_PREFIX.length());
                tokenRepository.findByToken(auth)
                        .ifPresent(token -> {
                            req.setAttribute(AUTH_USER_ATTRIBUTE, token.getUser());
                            req.setAttribute(AUTH_TOKEN_ATTRIBUTE, token);
                        });
            }
        }
        filterChain.doFilter(req, res);
    }

}
