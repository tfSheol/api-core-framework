package Core.Singleton;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by teddy on 21/05/2016.
 */
public class PermsSingleton {
    private static PermsSingleton instance = new PermsSingleton();
    private ArrayList<HashMap<String, Object>> perms = new ArrayList<>();
    private Properties routes = new Properties();
    private File configFile = new File("route.properties");
    public static int MEMBER = 10;
    public static int MODO = 30;
    public static int ADMIN = 50;

    private PermsSingleton() {
        try {
            FileReader reader = new FileReader(configFile);
            routes.load(reader);
            reader.close();
            for (Map.Entry<Object, Object> e : routes.entrySet()) {
                addRoute(e.getKey().toString(), Integer.parseInt(e.getValue().toString()));
            }
        } catch (IOException ex) {
            System.err.println("IOException : " + ex);
        }
    }

    public static PermsSingleton getInstance() {
        return instance;
    }

    public void addRoute(String route, int minusGroup) {
        System.out.println("[SYSTEM] -> Load route: " + route + " - perm min level power: " + minusGroup);
        HashMap<String, Object> perm = new HashMap<>();
        perm.put("route", route);
        perm.put("group", minusGroup);
        perms.add(perm);
    }

    public boolean checkRouteWithoutPerms(String route) {
        for (HashMap<String, Object> perm : perms) {
            if (perm.get("route").equals(route)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkRouteWithPerms(String route, int group) {
        for (HashMap<String, Object> perm : perms) {
            if (perm.get("route").equals(route) && (int) perm.get("group") <= group) {
                return true;
            }
        }
        return checkRouteWithoutPerms(route);
    }
}
