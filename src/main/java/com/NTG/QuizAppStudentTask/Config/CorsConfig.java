package com.NTG.QuizAppStudentTask.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {

            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**")
                            // أو "/quiz/**" لو عايزة تدقيها
                            .allowedOrigins("http://localhost:4200")
                            .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                            .allowedHeaders("Authorization","Content-Type","Accept","Origin","X-Requested-With")
                            .allowCredentials(true)
                            .maxAge(3600);
                }
            };
    }
}