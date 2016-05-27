package Core;

import Core.Http.Code;
import Core.Http.Error;
import Core.Http.Oauth2;
import Core.Http.Oauth2Permissions;
import Core.Singleton.ServerSingleton;
import Core.Singleton.UserSecuritySingleton;
import Plugin.Path;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by teddy on 04/05/2016.
 */
public class Router {
    private HashMap<String, Object> args = new HashMap<>();

    public String find(String socket, String method, String route, HashMap<String, String> headerField, JSONObject jsonObject) {
        Class<Path> obj = Path.class;
        Gson gson = new Gson();
        Oauth2 oauth2 = new Oauth2((headerField.containsKey("Authorization")) ? headerField.get("Authorization") : null);
        Oauth2Permissions oauth2Permissions = new Oauth2Permissions();
        if ((!route.equals("/oauth")) || (oauth2.getType() != null && oauth2.getType().equals(Oauth2.BASIC) && route.equals("/oauth"))) {
            if (oauth2Permissions.checkPermsRoute(socket, oauth2, method, route, obj, oauth2.getType())) {
                for (Method methods : obj.getDeclaredMethods()) {
                    if (methods.isAnnotationPresent(Route.class) && methods.isAnnotationPresent(Methode.class)) {
                        if (parseRouteParameters(methods.getAnnotation(Route.class).value(), route) && methods.getAnnotation(Methode.class).value().equals(method)) {
                            try {
                                System.out.println("[SERVER] -> " + socket + " execute " + route);
                                Object[] params = {socket, oauth2, headerField, jsonObject, args};
                                Object returnObj = methods.invoke(obj.newInstance(), params);
                                String json = gson.toJson(returnObj);
                                System.out.println("[SERVER] -> " + json);
                                return json;
                            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                                System.err.println("[SERVER] -> " + socket + " error on route finder : " + e);
                            }
                        }
                    }
                }
            } else {
                ServerSingleton.getInstance().setHttpCode(socket, Code.UNAUTHORIZED);
                String json = gson.toJson(new Error(socket, method, route, Code.UNAUTHORIZED));
                System.out.println("[SERVER] -> " + json);
                return json;
            }
            UserSecuritySingleton.getInstance().setUserOffline(socket);
        }
        ServerSingleton.getInstance().setHttpCode(socket, Code.METHOD_NOT_ALLOWED);
        String json = gson.toJson(new Error(socket, method, route, Code.METHOD_NOT_ALLOWED));
        System.out.println("[SERVER] -> " + json);
        return json;
    }

    private boolean parseRouteParameters(String path, String route) {
        String[] pathArray = path.split("/");
        String[] routeArray = route.split("/");
        if (pathArray.length == routeArray.length) {
            for (int i = 0; i < pathArray.length; i++) {
                if (!pathArray[i].equals(routeArray[i])) {
                    if (pathArray[i].matches("\\{(.*?)\\}")) {
                        args.put(pathArray[i].replace("{", "").replace("}", ""), routeArray[i]);
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
