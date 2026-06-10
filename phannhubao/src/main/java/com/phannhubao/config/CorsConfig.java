package com.phannhubao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:8080",
                "http://172.16.6.205:*",
                "http://192.168.*:*",
                "https://*.trycloudflare.com",
                "https://*.ngrok-free.app",
                "https://*.ngrok.io",
                "https://*.web.app",
                "https://*.firebaseapp.com"
        ));
        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "ngrok-skip-browser-warning"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
