package org.soulsoftware.spigot.core.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private final Properties properties;

    public PropertiesReader(String file) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(file);
        this.properties = new Properties();
        try {
            this.properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }

    public String fromValue(String value) {
        for (Object key : properties.keySet()) {
            if (properties.get(key).equals(value)) return String.valueOf(key);
        }
        return null;
    }
}