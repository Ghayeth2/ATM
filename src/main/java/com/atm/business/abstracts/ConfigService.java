package com.atm.business.abstracts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public interface ConfigService {
    void updateProperty(String key, String value) throws IOException;
    Properties getProperties() throws IOException;
    void updateLeadTailNumbers(String lead, String tail) throws IOException;
    void replaceMsgParameter(String param, String value, String property);
}
