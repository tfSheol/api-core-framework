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
    private int permsNb = 0;
    public static int MEMBER = 10;
    public static int MODO = 30;
    public static int ADMIN = 50;

    private PermsSingleton() {
        try {
            FileReader reader = new FileReader(configFile);
            routes.load(reader);
            reader.close();
            for (Map.Entry<Object, Object> e : routes.entrySet()) {
                String[] routeData = getDataRouting(e.getKey().toString());
                if (routeData != null) {
                    addRoute(routeData[0], routeData[1], Integer.parseInt(e.getValue().toString()));
                }
            }
            ServerSingleton.getInstance().log("[SYSTEM] -> Nb perms loaded: " + permsNb);
        } catch (IOException ex) {
            ServerSingleton.getInstance().log("IOException : " + ex, true);
        }
    }

    public static PermsSingleton getInstance() {
        return instance;
    }

    public ArrayList<HashMap<String, Object>> getPerms() {
        return perms;
    }

    public void addRoute(String method, String route, int minusGroup) {
        ServerSingleton.getInstance().log("[PERM] -> method: " + method + " - route: " + route + " - minimum power: " + minusGroup);
        HashMap<String, Object> perm = new HashMap<>();
        perm.put("method", method);
        perm.put("route", route);
        perm.put("group", minusGroup);
        perms.add(perm);
        permsNb++;
    }

    public boolean checkRouteWithoutPerms(String method, String route) {
        for (HashMap<String, Object> perm : perms) {
            if (perm.get("route").equals(route) && perm.get("method").equals(method)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkRouteWithPerms(String method, String route, int group) {
        for (HashMap<String, Object> perm : perms) {
            if ((perm.get("route").equals(route) && perm.get("method").equals(method)) && (int) perm.get("group") <= group) {
                return true;
            }
        }
        return checkRouteWithoutPerms(method, route);
    }

    private String[] getDataRouting(String routeData) {
        if (routeData.matches("\\[(.*?)\\](.*?)")) {
            return routeData.replaceAll("\\[", "").split("]");
        }
        return null;
    }
}
