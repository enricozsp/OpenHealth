package com.openhealth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openHealthOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OpenHealth API")
                        .description("Wallet pessoal de saúde - Meu Prontuário. "
                                + "API REST para gestão de perfil do paciente, anamnese, alergias, "
                                + "vacinas, cirurgias, consultas, exames, autenticação e compartilhamento com médicos.")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact().name("OpenHealth"))
                        .license(new License().name("Proprietary")))
                .components(new Components()
                        .addSecuritySchemes("sessionCookie",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("JSESSIONID")
                                        .description("Sessão HTTP estabelecida via /api/auth/login ou /api/doctor-auth/login")));
    }

    @Bean
    public GroupedOpenApi patientApi() {
        return GroupedOpenApi.builder()
                .group("paciente")
                .pathsToMatch("/api/auth/**", "/api/profile/**", "/api/anamnese/**",
                        "/api/allergies/**", "/api/vaccines/**", "/api/surgeries/**",
                        "/api/consultations/**", "/api/exams/**", "/api/sharing/**")
                .build();
    }

    @Bean
    public GroupedOpenApi doctorApi() {
        return GroupedOpenApi.builder()
                .group("medico")
                .pathsToMatch("/api/doctor-auth/**", "/api/doctor/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("todos")
                .pathsToMatch("/api/**")
                .build();
    }
}