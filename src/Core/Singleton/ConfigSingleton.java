package Core.Singleton;

import java.io.*;
import java.util.Properties;

/**
 * Created by teddy on 04/05/2016.
 */
public class ConfigSingleton {
    private static ConfigSingleton instance = new ConfigSingleton();
    private Properties props = new Properties();
    private File configFile = new File("config.properties");

    private ConfigSingleton() {
        try {
            FileReader reader = new FileReader(configFile);
            props.load(reader);
            reader.close();
        } catch (IOException ex) {
            System.err.println("IOException : " + ex);
        }
    }

    public static ConfigSingleton getInstance() {
        return instance;
    }

    public Properties getProps() {
        return props;
    }

    public String getPropertie(String key) {
        return props.getProperty(key);
    }

    public int getPort() {
        return Integer.parseInt(props.getProperty("port"));
    }

    public int getSocketTimeout() {
        return Integer.parseInt(props.getProperty("socket_timeout"));
    }

    public String getName() {
        return String.valueOf(props.getProperty("name"));
    }

    public String getVersion() {
        return String.valueOf(props.getProperty("version"));
    }

    public long getTokenExpires() {
        return Long.valueOf(props.getProperty("token_expires"));
    }

    public int getSalt() {
        return Integer.parseInt(props.getProperty("salt"));
    }

    public void setProps(String key, String value, String store) {
        try {
            props.setProperty(key, value);
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, store);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
