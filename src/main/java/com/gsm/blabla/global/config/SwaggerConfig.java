package com.gsm.blabla.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("Blabla API")
            .version("1.0.0")
            .contact(new Contact().name("랜딩 페이지").url("https://blablah.net/"));

        return new OpenAPI()
            .components(new Components())
            .info(info);
    }
}
