package com.africahr.leave.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.client.registration.microsoft.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.microsoft.redirect-uri}")
    private String redirectUri;

    private static final String TENANT_ID = "5b301832-19fe-47c4-adc5-f3ad59ff681c";

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "oauth2";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Leave Management System API")
                        .description("API documentation for the Leave Management System")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl("https://login.microsoftonline.com/" + TENANT_ID + "/oauth2/v2.0/authorize")
                                                        .tokenUrl("https://login.microsoftonline.com/" + TENANT_ID + "/oauth2/v2.0/token")
                                                        .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                                .addString("openid", "OpenID Connect")
                                                                .addString("profile", "User Profile")
                                                                .addString("email", "Email Address"))))));
    }
} 