package Core.Singleton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by teddy on 04/05/2016.
 */
public class ConfigSingleton {
    private static ConfigSingleton instance = new ConfigSingleton();
    private Properties props = new Properties();
    private File configFile = new File("config.properties");

    private ConfigSingleton() {
        reload();
    }

    public void reload() {
        try {
            FileReader reader = new FileReader(configFile);
            props.clear();
            props.load(reader);
            reader.close();
        } catch (IOException e) {
            ServerSingleton.getInstance().log("IOException : " + e, e);
        }
    }

    public static ConfigSingleton getInstance() {
        return instance;
    }

    public Properties getProps() {
        return props;
    }

    public String getString(String key) {
        return props.getProperty(key);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    public boolean isSSL() {
        return Boolean.parseBoolean(props.getProperty("ssl"));
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

    public String getAuthor() {
        return String.valueOf(props.getProperty("author"));
    }

    public String getVersion() {
        return String.valueOf(props.getProperty("version"));
    }

    public long getTokenExpires() {
        return Long.valueOf(props.getProperty("token_expires"));
    }

    public String getSalt() {
        return props.getProperty("salt");
    }

    public String getMaxAttempt() {
        return props.getProperty("max_attempt");
    }

    public String getCharset() {
        return props.getProperty("charset");
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