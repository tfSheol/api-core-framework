package Core.Singleton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by teddy on 29/05/2016.
 */
public class IpSingleton {
    private static IpSingleton instance = new IpSingleton();
    private Properties props = new Properties();
    private File ipFile = new File("ip.properties");
    private ArrayList<HashMap<String, Object>> ipList = new ArrayList<>();

    private IpSingleton() {
        try {
            FileReader reader = new FileReader(ipFile);
            props.load(reader);
            reader.close();
        } catch (IOException ex) {
            ServerSingleton.getInstance().log("IOException : " + ex, true);
        }
    }

    public static IpSingleton getInstance() {
        return instance;
    }

    public void setIpFail(String ip) {
        if (!isBanned(ip) && !isWhiteListed(ip)) {
            boolean founded = false;
            for (int i = 0; i < ipList.size(); i++) {
                if (ipList.get(i).get("ip").equals(ip)) {
                    if (((Integer) ipList.get(i).get("attempt")) > Integer.parseInt(ConfigSingleton.getInstance().getMaxAttempt())) {
                        banIp(ip);
                        ipList.remove(i);
                        i--;
                    } else {
                        ipList.get(i).replace("attempt", (Integer) ipList.get(i).get("attempt") + 1);
                    }
                    founded = true;
                }
            }
            if (!founded) {
                HashMap<String, Object> newIp = new HashMap<>();
                newIp.put("ip", ip);
                newIp.put("attempt", 1);
                ipList.add(newIp);
            }
        }
    }

    public boolean isBanned(String ip) {
        reloadIp();
        return props.containsKey(ip) && props.get(ip).equals("false");
    }

    public boolean isWhiteListed(String ip) {
        reloadIp();
        return props.containsKey(ip) && props.get(ip).equals("true");
    }

    public void reloadIp() {
        try {
            FileReader reader = new FileReader(ipFile);
            props.load(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void banIp(String ip) {
        try {
            props.setProperty(ip, String.valueOf(false));
            FileWriter writer = new FileWriter(ipFile);
            props.store(writer, "IP");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
