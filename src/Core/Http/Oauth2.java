package Core.Http;

import Core.Singleton.UserSecuritySingleton;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created by teddy on 21/05/2016.
 */
public class Oauth2 {
    public static String BASIC = "Basic";
    public static String BEARER = "Bearer";
    private String type;
    private String value;
    private String token;
    private String username;
    private String password;

    public Oauth2(String authorization) {
        if (authorization != null) {
            String[] tmp = authorization.split(" ");
            if (tmp.length == 2) {
                type = tmp[0];
                value = tmp[1];
                if (type.equals(BASIC)) {
                    if (value.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$")) {
                        tmp = new String(Base64.getDecoder().decode(value)).split(":");
                        if (tmp.length == 2) {
                            username = tmp[0];
                            password = tmp[1];
                        }
                    }
                } else if (type.equals(BEARER)) {
                    token = value;
                }
            }
        }
    }

    public boolean login(String socket) {
        return UserSecuritySingleton.getInstance().checkUser(socket, username, password);
    }

    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public boolean isLogin() {
        return username.isEmpty() && password.isEmpty();
    }

    public boolean isToken() {
        return token.isEmpty();
    }
}
