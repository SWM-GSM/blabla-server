package com.gsm.blabla.global.webhook;

import com.gsm.blabla.global.webhook.service.WebhookService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebhookConfiguration {

    @Bean
    public WebhookService webhookService() {
        return new WebhookService();
    }
}
