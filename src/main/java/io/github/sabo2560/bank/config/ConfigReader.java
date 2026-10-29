package io.github.sabo2560.bank.config;
import io.github.sabo2560.bank.exception.ConfigException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new ConfigException("config.properties not found");
            }
            PROPERTIES.load(in);
        } catch (IOException e) {
            throw new ConfigException("Could not read config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key){
        String v = PROPERTIES.getProperty(key);
        if(v == null){
            throw new ConfigException("Missing config key: " + key);
        }
        return v;

    }
    public static double getDouble(String key){
        String text = get(key);
        try{

            return  Double.parseDouble(text);

        } catch (NumberFormatException e) {
            throw new ConfigException("Config value for " + key + " is not a number: " + text, e);
        }

    }
}