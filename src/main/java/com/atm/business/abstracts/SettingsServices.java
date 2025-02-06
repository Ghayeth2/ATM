package com.atm.business.abstracts;

import java.util.Map;

/**
 * Used to manipulate dynamically set configurations
 * for page size, fees rate, and account's generated numbers.
 */
public interface SettingsServices {
    Map<String, String> updateAndGetAll(Map<String, String> settings);
    public Map<String, String> getAllValues();
}
