package aa.billing.config;

import aa.common.auth.AuthenticatedUser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    public static final String SECURITY_REQUIREMENT_JWT = "auth-jwt";

    @Bean
    public OpenAPI openApi() {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(
                AuthenticatedUser.class);
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SECURITY_REQUIREMENT_JWT,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                );
    }
}
