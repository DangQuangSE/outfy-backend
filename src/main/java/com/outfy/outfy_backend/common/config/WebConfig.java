package com.outfy.outfy_backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve model files from /models/** path
        registry.addResourceHandler("/models/**")
                .addResourceLocations("classpath:/static/models/");

        // Serve preview files from /previews/** path
        registry.addResourceHandler("/previews/**")
                .addResourceLocations("classpath:/static/previews/");
    }
}

