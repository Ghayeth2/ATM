package com.atm.business.abstracts;

import java.io.IOException;

public interface ConfigService {
    void updateProperty(String key, String value) throws IOException;
    void updateLeadTailNumbers(String lead, String tail) throws IOException;
    void replaceMsgParameter(String param, String value, String property);
}
