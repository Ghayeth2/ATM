package com.atm.business.concretes;

import com.atm.business.abstracts.ConfigService;
import com.atm.business.abstracts.SettingsServices;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A service to control the settings of the app
 * such as pages sizes for accounts, transactions and admin transaction
 * monitoring page.
 */
@Service @Log4j2
@RequiredArgsConstructor
public class SettingsManager implements SettingsServices {
    private final ConfigService configService;

    /**
     * Updates values of sent keys for app settings, and returns same
     * key, value pairs as response of updated data.
     * @param settings
     * @return
     */
    @Override @SneakyThrows
    public Map<String, String> updateAndGetAll(Map<String, String> settings) {
        // Adding logger
        log.info("SettingsManager -> updateAndGetAll is running...");
        // Iterating through each key-value pairs and updating them in config
        settings.forEach((key, value) -> {
            try {
                configService.updateProperty(key, value);
                log.info(key + " is updated to " + value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Returning same key,value pairs as response (updated settings)
        return settings;
    }


    /**
     *
     * @return All values of app settings once settings page is loaded.
     */
    @Override @SneakyThrows
    public Map<String, String> getAllValues() {
        // Adding logger
        log.info("SettingsManager -> getAllValues is running...");
        Map<String, String> map = new HashMap<>();
        map.put("account.tail.number", configService
                .getProperties().getProperty("account.tail.number"));

        map.put("account.lead.number", configService
                .getProperties().getProperty("account.lead.number"));
        map.put("accounts.page.size", configService
                .getProperties().getProperty("accounts.page.size"));
        map.put("transactions.page.size", configService
        .getProperties().getProperty("transactions.page.size"));
        map.put("users.page.size", configService
        .getProperties().getProperty("users.page.size"));
        map.put("transactions.fees.personal", configService
        .getProperties().getProperty("transactions.fees.personal"));
        map.put("transactions.fees.savings", configService
        .getProperties().getProperty("transactions.fees.savings"));
        map.put("transactions.fees.business", configService
        .getProperties().getProperty("transactions.fees.business"));
        return map;
    }
}
