package com.atm.business.concretes;

import com.atm.business.abstracts.ConfigService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service @Log4j2
public class ConfigManager implements ConfigService {


    @Override
    public void updateProperty(String key, String value) throws IOException {
        Properties prop = getProperties();

        prop.setProperty(key, value);
        // Image's WORKDIR where the properties file will be copied to instead of jar
        File file = new File("/atmsemu/dynamic-configs.properties");

        // Write the updated properties back to the file
        try (OutputStream output = new FileOutputStream(file)) {
            prop.store(output, null);  // Optionally, add a comment
        }
    }

    @Override
    public Properties getProperties() throws IOException {
        Properties prop = new Properties();

        File file = new File("/atmsemu/dynamic-configs.properties");

        try (InputStream input = new FileInputStream(file)) {
            prop.load(input);
        }
        return prop;
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
