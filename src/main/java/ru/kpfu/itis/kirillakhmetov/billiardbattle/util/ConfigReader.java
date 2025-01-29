package ru.kpfu.itis.kirillakhmetov.billiardbattle.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigReader {
    private static final String NAME_APPLICATION_FILE = "/ru/kpfu/itis/kirillakhmetov/billiardbattle/properties/application-dev.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream input = ConfigReader.class.getResourceAsStream(NAME_APPLICATION_FILE)) {
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getValue(String key) {
        return PROPERTIES.getProperty(key);
    }
}