package Core;

import Core.Http.*;
import Core.Http.Error;
import Core.Singleton.IpSingleton;
import Core.Singleton.ServerSingleton;
import Core.Singleton.UserSecuritySingleton;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by teddy on 04/05/2016.
 */
public class Router {
    private Map args = new Map();

    public String find(String socket, String method, String route, Header headerField, JSONObject jsonObject) {
        for (Class<?> obj : ServerSingleton.getInstance().getAnnotated()) {
            Oauth2 oauth2 = new Oauth2((headerField.containsKey("Authorization")) ? headerField.getString("Authorization") : null);
            Oauth2Permissions oauth2Permissions = new Oauth2Permissions();
            if ((!route.equals("/oauth")) || (oauth2.getType() != null && oauth2.getType().equals(Oauth2.BASIC) && route.equals("/oauth"))) {
                if (oauth2Permissions.checkPermsRoute(socket, oauth2, method, route, obj, oauth2.getType())) {
                    for (Method methods : obj.getDeclaredMethods()) {
                        if (methods.isAnnotationPresent(Route.class) && methods.isAnnotationPresent(Methode.class)) {
                            if (parseRouteParameters(methods.getAnnotation(Route.class).value(), route) && methods.getAnnotation(Methode.class).value().equals(method)) {
                                try {
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> " + socket + " execute " + route);
                                    Object[] params = {socket, oauth2, headerField, jsonObject, args};
                                    String json = cleanJson(methods.invoke(obj.newInstance(), params)).toString();
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> " + json);
                                    return json;
                                } catch (IllegalAccessException | InvocationTargetException | InstantiationException | JSONException e) {
                                    ServerSingleton.getInstance().log("[SERVER] -> " + socket + " error on route finder : " + e, true);
                                }
                            }
                        }
                    }
                } else {
                    ServerSingleton.getInstance().setHttpCode(socket, Code.UNAUTHORIZED);
                    String json = cleanJson(new Error(socket, method, route, Code.UNAUTHORIZED)).toString();
                    ServerSingleton.getInstance().log(socket, "[SERVER] -> " + json);
                    return json;
                }
                UserSecuritySingleton.getInstance().setUserOffline(socket);
            }
        }
        ServerSingleton.getInstance().setHttpCode(socket, Code.METHOD_NOT_ALLOWED);
        String json = cleanJson(new Error(socket, method, route, Code.METHOD_NOT_ALLOWED)).toString();
        ServerSingleton.getInstance().log(socket, "[SERVER] -> " + json);
        IpSingleton.getInstance().setIpFail(socket.split(":")[0].replace("/", ""));
        return json;
    }

    private JSONObject cleanJson(Object obj) {
        JSONObject json = new JSONObject(new Gson().toJson(obj));
        if (!json.isNull("make")) {
            json.remove("make");
        }
        if (!json.isNull("data") && json.getJSONArray("data").length() == 0) {
            json.remove("data");
        }
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

    public static Object getJson(Object obj) {
        try {
            return new JSONObject(new Gson().toJson(obj));
        } catch (JSONException ex) {
            try {
                return new JSONArray(new Gson().toJson(obj));
            } catch (JSONException ex1) {
                return null;
            }
        }
    }
}
