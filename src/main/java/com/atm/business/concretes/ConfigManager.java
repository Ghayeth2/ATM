package com.atm.business.concretes;

import com.atm.business.abstracts.ConfigService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service @Log4j2
public class ConfigManager implements ConfigService {

    @Override
    public void updateProperty(String key, String value) throws IOException {
        Properties prop = getProperties();

        prop.setProperty(key, value);

        try (OutputStream output = new FileOutputStream("src/main/resources/dynamic-configs.properties")) {
            prop.store(output, null);
        }
    }

    @Override
    public Properties getProperties() throws IOException {
        Properties prop = new Properties();
        try(InputStream input = new FileInputStream("src/main/resources/dynamic-configs.properties")){
            prop.load(input);
        }
        return prop;
    }

    @Override
    public void updateLeadTailNumbers(String lead, String tail) throws IOException {
        if (tail.contentEquals("9999")){
            log.info("numbers in contentEquals(\"9999\") tail "+tail);
            updateProperty("account.tail.number", "0000");
            int leadN = Integer.parseInt(lead);
            leadN++;
            lead = String.format("%04d", leadN);
            log.info("numbers in contentEquals(\"9999\") lead "+lead);
            updateProperty("account.lead.number", lead);
        } else {
            int tailN = Integer.parseInt(tail);
            tailN++;
            tail = String.format("%04d", tailN);
            log.info("numbers in else config servis "+tail);
            updateProperty("account.tail.number", tail);
        }
    }

    // Used to set param within successes.properties file
    // scs.accounts=...${param}....
    @Override
    public void replaceMsgParameter(String param, String value, String property) {
        Properties messages = new Properties();
        Map<String, String> m = new HashMap<>();
        m.put(param, value);
        StrSubstitutor sub = new StrSubstitutor(m);
        String msg = sub.replace(messages.getProperty(property));
    }
}
