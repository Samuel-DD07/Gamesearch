package fr.epita.apping.fullstack.gamesearch.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    final String bearerAuth = "Bearer Authentication";
    final String apiKeyAuth = "ApiKey Authentication";

    return new OpenAPI()
        .info(
            new Info()
                .title("GameSearch API")
                .version("1.0.0")
                .description(
                    "API interactive pour le moteur de recherche de jeux vidéo GameSearch.")
                .contact(new Contact().name("Équipe Développement Epita")))
        .addSecurityItem(new SecurityRequirement().addList(bearerAuth).addList(apiKeyAuth))
        .components(
            new Components()
                .addSecuritySchemes(
                    bearerAuth,
                    new SecurityScheme()
                        .name(bearerAuth)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSecuritySchemes(
                    apiKeyAuth,
                    new SecurityScheme()
                        .name("X-API-Key")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .description("Clé API pour les partenaires")));
  }
}
