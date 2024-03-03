package aa.auth.config;

import aa.auth.repository.AuthTokenRepository;
import aa.auth.repository.AuthUserRepository;
import aa.auth.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public TokenService tokenService(
            AuthUserRepository userRepository,
            AuthTokenRepository tokenRepository) {
        return new TokenService(userRepository, tokenRepository);
    }

}
