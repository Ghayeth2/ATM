package com.atm.core.config;

import com.atm.core.audit.AuditorAwareTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestAuditingConfig {
    @Bean
    @Primary
    public AuditorAwareTest auditorAware() {
        return new AuditorAwareTest();
    }
}
