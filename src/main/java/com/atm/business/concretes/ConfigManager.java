package com.atm.business.concretes;

import com.atm.business.abstracts.ConfigService;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
