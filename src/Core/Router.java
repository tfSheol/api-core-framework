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
            Oauth2 oauth2 = new Oauth2((headerField.containsKey("authorization")) ? headerField.getString("authorization") : null);
            Oauth2Permissions oauth2Permissions = new Oauth2Permissions();
            if (!method.equals("OPTIONS") && oauth2.isToken()) {
                UserSecuritySingleton.getInstance().updateSocketToken(socket, oauth2.getToken());
            }
            if (route != null && (!route.equals("/oauth") || (oauth2.getType() != null && oauth2.getType().equals(Oauth2.BASIC) && route.equals("/oauth")))) {
                if (oauth2Permissions.checkPermsRoute(socket, oauth2, method, route, obj, oauth2.getType())) {
                    for (Method methods : obj.getDeclaredMethods()) {
                        if (methods.isAnnotationPresent(Route.class) && methods.isAnnotationPresent(Methode.class)) {
                            if (methods.getAnnotation(Route.class).value().equals(route) && methods.getAnnotation(Methode.class).value().equals(method)) {
                                try {
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> response " + error.getCode() + " " + error.getError() + " | " + ServerSingleton.getInstance().getHttpCode(socket));
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> execute " + route);
                                    Object[] params = {socket, oauth2, headerField, jsonObject, args};
                                    String json = cleanJson(socket, method, methods.invoke(obj.newInstance(), params)).toString();
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> " + json);
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> response " + error.getCode() + " " + error.getError() + " | " + ServerSingleton.getInstance().getHttpCode(socket));
                                    return json;
                                } catch (IllegalAccessException | InstantiationException e) {
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> error route not founded: ", e);
                                } catch (InvocationTargetException e) {
                                    ServerSingleton.getInstance().log(socket, "[SERVER] -> router: ", e);
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
        IpSingleton.getInstance().setIpFail(socket);
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
        JSONObject json_tmp = new JSONObject(json.toString());
        removeEmptyValuesJSONObject(json_tmp, json);
        return json;
    }

    private void removeEmptyValuesJSONObject(JSONObject json_tmp, JSONObject json) {
        for (Object object : json_tmp.keySet()) {
            //System.err.println(object + " : " + json.get(object.toString()).getClass().getTypeName());
            if (json_tmp.get(object.toString()).getClass().getTypeName().equals("org.json.JSONArray")) {
                removeEmptyValuesJSONArray(json_tmp.getJSONArray(object.toString()), json.getJSONArray(object.toString()));
            } else if ((json_tmp.get(object.toString()).getClass().getTypeName().equals("java.lang.Integer") && (json_tmp.getInt(object.toString()) == -1 || json_tmp.getInt(object.toString()) == 0))
                    || (json_tmp.get(object.toString()).getClass().getTypeName().equals("java.lang.String") && json_tmp.getString(object.toString()) == null)
                    || (json_tmp.get(object.toString()).getClass().getTypeName().equals("java.lang.Long") && (json_tmp.getInt(object.toString()) == -1 || json_tmp.getInt(object.toString()) == 0))) {
                json.remove(object.toString());
            }
        }
    }

    private void removeEmptyValuesJSONArray(JSONArray json_tmp, JSONArray json) {
        /*for (int i = 0; i < json_tmp.length(); i++) {
            removeEmptyValuesJSONObject();
        }*/
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
