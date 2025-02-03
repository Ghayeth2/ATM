package com.atm.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Used to pass the security measures of web browsers to
 * open receipt files from the local path.
 * This config works for both local and remote environment
 * as long as keep modifying location of resources.
 * Whether from local machine or Docker volumes.
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from an external directory (e.g., D:/files/receipts/)
        registry.addResourceHandler("/receipts/**")
                .addResourceLocations("file:/D:/files/receipts/");
    }
}

