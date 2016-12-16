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
        } catch (IOException e) {
            ServerSingleton.getInstance().log("IOException : " + e, e);
        }
    }

    public static IpSingleton getInstance() {
        return instance;
    }

    public String convertToIp(String socket) {
        return new StringBuilder(new StringBuilder(socket).reverse().toString().replace("/", "").replaceFirst(":", " ")).reverse().toString().split(" ")[0];
    }

    public void setIpFail(String socket) {
        String ip = convertToIp(socket);
        if (!isBanned(ip) && !isWhiteListed(ip)) {
            boolean founded = false;
            for (int i = 0; i < ipList.size(); i++) {
                if (ipList.get(i).get("ip").equals(ip)) {
                    ServerSingleton.getInstance().log("[SERVER BAN IP] -> " + socket + " as a suspicious behavior (attempt: " + ipList.get(i).get("attempt") + "/" + Integer.parseInt(ConfigSingleton.getInstance().getMaxAttempt()) + ")");
                    if (((Integer) ipList.get(i).get("attempt")) >= Integer.parseInt(ConfigSingleton.getInstance().getMaxAttempt())) {
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
                ServerSingleton.getInstance().log("[SERVER BAN IP] -> " + socket + " as a suspicious behavior and " + ip + " as added to watch list");
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
            props.clear();
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
            ServerSingleton.getInstance().log("[SERVER BAN IP] -> " + ip + " as been banned!");
        } catch (IOException e) {
            ServerSingleton.getInstance().log("[SERVER BAN IP] -> Exception occurred while ban ip " + ip, e);
        }
    }

    public void unbanIp(String ip) {
        try {
            props.remove(ip);
            FileWriter writer = new FileWriter(ipFile);
            props.store(writer, "IP");
            writer.close();
            ServerSingleton.getInstance().log("[SERVER BAN IP] -> " + ip + " as been unbanned!");
        } catch (IOException e) {
            ServerSingleton.getInstance().log("[SERVER BAN IP] -> Exception occurred while unban ip " + ip, e);
        }
    }

    public void whiteListIp(String ip) {
        try {
            props.setProperty(ip, String.valueOf(true));
            FileWriter writer = new FileWriter(ipFile);
            props.store(writer, "IP");
            writer.close();
            ServerSingleton.getInstance().log("[SERVER BAN IP] -> " + ip + " as been white listed!");
        } catch (IOException e) {
            ServerSingleton.getInstance().log("[SERVER BAN IP] -> Exception occurred while white listed ip " + ip, e);
        }
    }
}
