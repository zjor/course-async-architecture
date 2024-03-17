package aa.billing.auth;

import aa.billing.repository.AccountRepository;
import aa.common.auth.AuthServerClient;
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
    public static final String BEARER_PREFIX = "Bearer ";

    private final AuthServerClient authServerClient;
    private final AccountRepository accountRepository;

    public AuthFilter(AuthServerClient authServerClient, AccountRepository accountRepository) {
        this.authServerClient = authServerClient;
        this.accountRepository = accountRepository;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        String auth = httpReq.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null) {
            var a = authServerClient.verify(auth.substring(BEARER_PREFIX.length()));
            req.setAttribute(AUTH_USER_ATTRIBUTE, accountRepository.ensure(a.id(), a.login(), a.role()));
        }
        filterChain.doFilter(req, res);
    }

}
