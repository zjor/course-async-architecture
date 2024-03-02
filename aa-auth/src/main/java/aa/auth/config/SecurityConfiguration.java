package aa.auth.config;

import aa.auth.repository.UserRepository;
import aa.auth.service.JpaRegisteredUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public RegisteredClientRepository registeredClientRepository(UserRepository userRepository) {
        return new JpaRegisteredUserRepository(userRepository);
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

}
