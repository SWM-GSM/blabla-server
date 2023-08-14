package com.gsm.blabla.global.webhook.service;

import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class WebhookService {

    @Value("${discord.webhook.url.dev}")
    private String devWebhookUrl;

    @Value("${discord.webhook.url.prod}")
    private String prodWebhookUrl;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public void sendErrorLog(String title, String description) {
        String webhookUrl = "";
        if (activeProfile.equals("local")) {
            return;
        }

        JSONObject data = new JSONObject();
        Map<String, String> embed = Map.of(
            "title", "에러 로그",
            "description", description
        );

        data.put("content", title);
        data.put("embeds", List.of(embed));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = new HttpEntity<>(data.toString(), headers);

        if (activeProfile.equals("dev")) {
            webhookUrl = devWebhookUrl;
        } else if (activeProfile.equals("prod")) {
            webhookUrl = prodWebhookUrl;
        }
        
        restTemplate.postForObject(webhookUrl, httpEntity, String.class);
    }
}
