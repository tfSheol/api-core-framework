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
import java.util.ArrayList;

/**
 * Created by teddy on 04/05/2016.
 */
public class Router {
    private Error error = new Error();
    private Map args = new Map();
    private ArrayList<String> genericRoutes = new ArrayList<>();
    private ArrayList<String> customRoutes = new ArrayList<>();

    public String find(String socket, String method, String querryRoute, Header headerField, JSONObject jsonObject) {
        error.setMethod(method);
        error.setPath(querryRoute);
        for (Class<?> obj : ServerSingleton.getInstance().getAnnotated()) {
            String route = getGenericRoute(method, querryRoute, obj);
            error.setPath(route);
            Oauth2 oauth2 = new Oauth2((headerField.containsKey("Authorization")) ? headerField.getString("Authorization") : null);
            Oauth2Permissions oauth2Permissions = new Oauth2Permissions();
            if (route != null && (!route.equals("/oauth") || (oauth2.getType() != null && oauth2.getType().equals(Oauth2.BASIC) && route.equals("/oauth")))) {
                if (oauth2Permissions.checkPermsRoute(socket, oauth2, method, route, obj, oauth2.getType())) {
                    for (Method methods : obj.getDeclaredMethods()) {
                        if (methods.isAnnotationPresent(Route.class) && methods.isAnnotationPresent(Methode.class)) {
                            if (methods.getAnnotation(Route.class).value().equals(route) && methods.getAnnotation(Methode.class).value().equals(method)) {
                                try {
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> execute " + route);
                                    Object[] params = {socket, oauth2, headerField, jsonObject, args};
                                    String json = cleanJson(socket, method, methods.invoke(obj.newInstance(), params)).toString();
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> " + json);
                                    return json;
                                } catch (IllegalAccessException | InstantiationException e) {
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> error on route finder : " + e, true);
                                } catch (InvocationTargetException e) {
                                    error.setErrorMsg(e.getTargetException().getMessage());
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> " + e.getTargetException().getMessage(), true);
                                }
                            }
                        }
                    }
                } else {
                    ServerSingleton.getInstance().setHttpCode(socket, Code.UNAUTHORIZED);
                    error.setCode(socket, Code.UNAUTHORIZED);
                    error.setErrorMsg("Full logging required or bad perms level");
                    String json = cleanJson(socket, method, error).toString();
                    ServerSingleton.getInstance().log(socket, "[SERVER] -> " + json);
                    return json;
                }
                UserSecuritySingleton.getInstance().setUserOffline(socket);
            } else {
                error.setErrorMsg("Route not founded");
            }
        }
        ServerSingleton.getInstance().setHttpCode(socket, Code.METHOD_NOT_ALLOWED);
        error.setCode(socket, Code.METHOD_NOT_ALLOWED);
        String json = cleanJson(socket, method, error).toString();
        ServerSingleton.getInstance().log(socket, "[SERVER] -> " + json);
        IpSingleton.getInstance().setIpFail(socket.split(":")[0].replace("/", ""));
        return json;
    }

    private JSONObject cleanJson(String socket, String method, Object obj) {
        JSONObject json = new JSONObject(new Gson().toJson(obj));
        if (!json.isNull("make")) {
            json.remove("make");
        }
        if (!json.isNull("id") && (json.get("id").equals(-1) || method.equals("GET"))) {
            json.remove("id");
        }
        if (!json.isNull("data") && json.getJSONArray("data").length() == 0) {
            json.remove("data");
            if (method.equals("GET") && json.get("code").equals(Code.OK)) {
                ServerSingleton.getInstance().setHttpCode(socket, Code.NOT_FOUND);
                json = json.put("code", Code.NOT_FOUND);
                json = json.put("error", "NOT FOUND");
                json = json.put("error_msg", "Data not found");
            }
        }
        return json;
    }

    private String getGenericRoute(String method, String route, Class<?> obj) {
        fullList(method, obj);
        String ret = getCurrentRoute(genericRoutes, route);
        if (ret == null) {
            return getCurrentRoute(customRoutes, route);
        }
        return ret;
    }

    private String getCurrentRoute(ArrayList<String> list, String route) {
        for (String currentRoute : list) {
            if (parseRouteParameters(currentRoute, route)) {
                return currentRoute;
            }
        }
        return null;
    }

    private void fullList(String method, Class<?> obj) {
        for (Method methods : obj.getDeclaredMethods()) {
            if (methods.isAnnotationPresent(Route.class) && methods.isAnnotationPresent(Methode.class) && methods.getAnnotation(Methode.class).value().equals(method)) {
                if (parseCustomRoute(methods.getAnnotation(Route.class).value())) {
                    customRoutes.add(methods.getAnnotation(Route.class).value());
                } else {
                    genericRoutes.add(methods.getAnnotation(Route.class).value());
                }
            }
        }
    }

    private boolean parseCustomRoute(String path) {
        int length = 0;
        String[] pathArray = path.split("/");
        for (String aPathArray : pathArray) {
            if (aPathArray.matches("\\{(.*?)\\}")) {
                length++;
            }
        }
        return length > 0;
    }

    private boolean parseRouteParameters(String path, String route) {
        int length = 0;
        int jok = 0;
        String[] pathArray = path.split("/");
        String[] routeArray = route.split("/");
        if (pathArray.length == routeArray.length) {
            for (int i = 0; i < pathArray.length; i++) {
                if (pathArray[i].equals(routeArray[i])) {
                    length++;
                } else if (pathArray[i].matches("\\{(.*?)\\}")) {
                    args.put(pathArray[i].replace("{", "").replace("}", ""), routeArray[i]);
                    jok++;
                }
            }
            if (pathArray.length == length + jok) {
                return true;
            }
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
