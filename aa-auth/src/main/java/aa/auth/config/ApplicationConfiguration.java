package aa.auth.config;

import aa.auth.ext.spring.auth.AuthFilter;
import aa.auth.repository.AuthTokenRepository;
import aa.auth.repository.AuthUserRepository;
import aa.auth.service.AuthTokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public AuthTokenService tokenService(
            AuthUserRepository userRepository,
            AuthTokenRepository tokenRepository) {
        return new AuthTokenService(userRepository, tokenRepository);
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistrationBean(AuthTokenRepository tokenRepository) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(tokenRepository));
        registrationBean.addUrlPatterns("/api/v1/auth/*");
        registrationBean.setOrder(0);
        return registrationBean;
    }

}
