package com.bruno.artistalbum.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Artist & Album API")
                        .version("1.0")
                        .description("API para gerenciamento de Artistas e Álbuns - Avaliação Full Stack"));
    }
}
