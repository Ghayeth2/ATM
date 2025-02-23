package com.atm.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * Used to pass the security measures of web browsers to
 * open receipt files from the local path.
 * This config works for both local and remote environment
 * as long as keep modifying location of resources.
 * Whether from local machine or Docker volumes.
 */
@Configuration @Slf4j
public class StaticResourceConfig implements WebMvcConfigurer {
    // Change for Production or Local env
    @Value("${static.resources.receipts.path}")
    private String receiptsPath;

    @Override
    public void addResourceHandlers(@SuppressWarnings("null") ResourceHandlerRegistry registry) {
        log.info("receiptsPath: "+receiptsPath);
        // Serve files from an external directory (e.g., D:/files/receipts/)
        registry.addResourceHandler("/receipts/**")
                .addResourceLocations("file:" + receiptsPath);
    }
}

