package aa.auth.config;

import aa.auth.ext.spring.auth.AuthFilter;
import aa.auth.repository.AuthTokenRepository;
import aa.auth.service.AuthTokenService;
import aa.common.ext.spring.aop.LoggingAspect;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public AuthTokenService tokenService(AuthTokenRepository tokenRepository) {
        return new AuthTokenService(tokenRepository);
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistrationBean(AuthTokenRepository tokenRepository) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(tokenRepository));
        registrationBean.addUrlPatterns("/api/v1/auth/*");
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

}
