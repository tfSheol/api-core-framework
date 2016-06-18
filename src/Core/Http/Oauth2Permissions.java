package Core.Http;

import Core.Singleton.PermsSingleton;
import Core.Singleton.UserSecuritySingleton;

/**
 * Created by teddy on 21/05/2016.
 */
public class Oauth2Permissions {
    public boolean checkPermsRoute(String socket, Oauth2 oauth2, String method, String route, Class<?> obj, String oauth2Type) {
        if (route != null) {
            if (oauth2.getType() != null) {
                if (oauth2Type.equals(Oauth2.BASIC) && route.equals("/oauth") && method.equals("POST")) {
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
        }
        return false;
    }
}
