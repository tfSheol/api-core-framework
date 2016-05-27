package Core.Http;

import Core.Route;
import Core.Singleton.PermsSingleton;
import Core.Singleton.UserSecuritySingleton;
import Plugin.Path;

import java.lang.reflect.Method;

/**
 * Created by teddy on 21/05/2016.
 */
public class Oauth2Permissions {
    public boolean checkPermsRoute(String socket, Oauth2 oauth2, String method, String userRoute, Class<Path> obj, String oauth2Type) {
        String route = getGenericRoute(userRoute, obj);
        if (oauth2.getType() != null) {
            if (oauth2Type.equals(Oauth2.BASIC) && userRoute.equals("/oauth") && method.equals("POST")) {
                return true;
            } else if (oauth2Type.equals(Oauth2.BEARER)) {
                if (PermsSingleton.getInstance().checkRouteWithoutPerms(method, route)) {
                    return true;
                } else if (UserSecuritySingleton.getInstance().checkToken(socket, oauth2.getToken()) &&
                        PermsSingleton.getInstance().checkRouteWithPerms(method, route, (int) UserSecuritySingleton.getInstance().getUserGroup(socket))) {
                    return true;
                }
            }
        } else {
            if (PermsSingleton.getInstance().checkRouteWithoutPerms(method, route)) {
                return true;
            }
        }
        return false;
    }

    private String getGenericRoute(String route, Class<Path> obj) {
        for (Method methods : obj.getDeclaredMethods()) {
            if (methods.isAnnotationPresent(Route.class) && parseRouteParameters(methods.getAnnotation(Route.class).value(), route)) {
                return methods.getAnnotation(Route.class).value();
            }
        }
        return null;
    }

    private boolean parseRouteParameters(String path, String route) {
        String[] pathArray = path.split("/");
        String[] routeArray = route.split("/");
        if (pathArray.length == routeArray.length) {
            for (int i = 0; i < pathArray.length; i++) {
                if (!pathArray[i].equals(routeArray[i]) && !pathArray[i].matches("\\{(.*?)\\}")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
