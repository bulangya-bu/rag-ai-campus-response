package com.example.webshiyan2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Ai ai = new Ai();
    private final Cors cors = new Cors();

    public Ai getAi() {
        return ai;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Ai {

        private String baseUrl;
        private String apiKey;
        private String model;
        private double temperature = 0.35;
        private int maxContextMessages = 10;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public int getMaxContextMessages() {
            return maxContextMessages;
        }

        public void setMaxContextMessages(int maxContextMessages) {
            this.maxContextMessages = maxContextMessages;
        }
    }

    public static class Cors {

        private List<String> allowedOrigins = new ArrayList<>();

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
