package com.soron.bot.whatsappbot.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {
a
    @Bean
    public OpenAIClient openAIClient() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null) {
            throw new IllegalStateException("OPENAI_API_KEY not set");
        }
        return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
    }
}

