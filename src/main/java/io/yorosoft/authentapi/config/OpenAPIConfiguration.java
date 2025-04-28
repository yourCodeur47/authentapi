package io.yorosoft.authentapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AuthentAPI")
                        .version("1.0")
                        .description("API d’authentification utilisateur")
                        .contact(new Contact()
                                .name("Ange Carmel YORO")
                                .email("yoropapers@outlook.fr")
                        )
                )
                .addServersItem(new Server().url("http://localhost:8080").description("Développement"));
    }
}
