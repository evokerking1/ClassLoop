package com.classloop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward app root to index.html
        registry.addViewController("/app")
                .setViewName("forward:/app/index.html");
        
        // Forward all non-API routes under /app to index.html for React routing
        registry.addViewController("/app/{path:[^\\.]*}")
                .setViewName("forward:/app/index.html");
        registry.addViewController("/app/{path:[^\\.]*}/{path2:[^\\.]*}")
                .setViewName("forward:/app/index.html");
    }
}
