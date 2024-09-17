package com.atm.business.concretes;

import com.atm.business.abstracts.ConfigService;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Service
public class ConfigManager implements ConfigService {

    @Override
    public void updateProperty(String key, String value) throws IOException {
        Properties prop = new Properties();
        String propertyFilePath = "src/main/resources/application.properties";
        prop.load(new FileInputStream(propertyFilePath));
        prop.setProperty(key, value);
        prop.store(new FileOutputStream(propertyFilePath), null);
    }

    @Override
    public void updateLeadTailNumbers(String lead, String tail) throws IOException {
        if (tail.contentEquals("9999")){
            updateProperty("account.tail.number", "0000");
            int leadN = Integer.parseInt(lead);
            leadN++;
            lead = String.format("%04d", leadN);
            updateProperty("account.lead.number", lead);
        } else {
            int tailN = Integer.parseInt(tail);
            tailN++;
            tail = String.format("%04d", tailN);
            updateProperty("account.tail.number", tail);
        }
    }
}
