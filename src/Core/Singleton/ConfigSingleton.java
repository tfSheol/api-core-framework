package Core.Singleton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by teddy on 04/05/2016.
 */
@SuppressWarnings("all")
public class ConfigSingleton {
    private final static String NO_VALUE = "no value setted!";
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
        return props.containsKey(key) ? props.getProperty(key) : "";
    }

    public boolean getBoolean(String key) {
        return props.containsKey(key) && Boolean.parseBoolean(props.getProperty(key));
    }

    public int getInt(String key) {
        return props.containsKey(key) ? Integer.parseInt(props.getProperty(key)) : 0;
    }

    public boolean isSSL() {
        return props.containsKey("ssl") && Boolean.parseBoolean(props.getProperty("ssl"));
    }

    public String getKeyStore() {
        return props.containsKey("key_store") ? props.getProperty("key_store") : "ssl/keystore.jks";
    }

    public String getKeyStorePassword() {
        return props.containsKey("key_store_password") ? props.getProperty("key_store_password") : "test1234";
    }

    public String getTrustStore() {
        return props.containsKey("trust_store") ? props.getProperty("trust_store") : "ssl/chain.jks";
    }

    public String getTrustStorePassword() {
        return props.containsKey("trust_store_password") ? props.getProperty("trust_store_password") : "test1234";
    }

    public int getPort() {
        return props.containsKey("port") ? Integer.parseInt(props.getProperty("port")) : 8080;
    }

    public int getSocketTimeout() {
        return props.containsKey("socket_timeout") ? Integer.parseInt(props.getProperty("socket_timeout")) : 10000;
    }

    public String getName() {
        return props.containsKey("name") ? String.valueOf(props.getProperty("name")) : NO_VALUE;
    }

    public String getAuthor() {
        return props.containsKey("author") ? String.valueOf(props.getProperty("author")) : NO_VALUE;
    }

    public String getVersion() {
        return props.containsKey("version") ? String.valueOf(props.getProperty("version")) : NO_VALUE;
    }

    public long getTokenExpires() {
        return props.containsKey("token_expires") ? Long.valueOf(props.getProperty("token_expires")) : 7200;
    }

    public String getSalt() {
        return props.containsKey("salt") ? props.getProperty("salt") : "DEFAULT";
    }

    public int getMaxAttempt() {
        return props.containsKey("max_attempt") ? Integer.valueOf(props.getProperty("max_attempt")) : 5;
    }

    public String getCharset() {
        return props.containsKey("charset") ? props.getProperty("charset") : "UTF-8";
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