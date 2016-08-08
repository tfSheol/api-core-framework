package Core.Singleton;

import Core.Http.Map;
import Core.Http.Oauth2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by teddy on 05/05/2016.
 */
public class UserSecuritySingleton {
    private static UserSecuritySingleton instance = new UserSecuritySingleton();
    private ArrayList<Map> users = new ArrayList<>();
    private int nbUsers = 0;

    public static UserSecuritySingleton getInstance() {
        return instance;
    }

    public static String hashMD5(String text) {
        return hash(text, "MD5");
    }

    public static String hashSHA1(String text) {
        return hash(text, "SHA-1");
    }

    public static String hash(String text, String hash) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(hash);
            md.update(text.concat(ConfigSingleton.getInstance().getSalt()).getBytes(ConfigSingleton.getInstance().getCharset()), 0, text.length());
            return toHex(md.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String toHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public int getNbUsers() {
        return nbUsers;
    }

    public void addUser(int id, String username, String password, int group) {
        Map user = new Map();
        user.put("username", username);
        user.put("password", password);
        user.put("id", id);
        user.put("group", group);
        user.put("socket", "");
        user.put("online", 0);
        user.put("token", "");
        user.put("expires_in", 0);
        nbUsers++;
        users.add(user);
    }

    public void updateUser(String socket, String key, Object value) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                user.replace(key, value);
            }
        }
    }

    public void updateFullUser(String socket, String username, String password) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                user.replace("username", username);
                user.replace("password", password);
            }
        }
    }

    public boolean checkUser(String socket, String username, String password) {
        for (Map user : users) {
            if (user.get("username").equals(username) && user.get("password").equals(hashSHA1(password))) {
                user.replace("socket", socket);
                user.replace("online", 1);
                user.replace("token", Oauth2.generateToken());
                user.replace("expires_in", System.currentTimeMillis() + (ConfigSingleton.getInstance().getTokenExpires() * 1000));
                return true;
            }
        }
        return false;
    }

    public boolean checkToken(String socket, String token) {
        for (Map user : users) {
            if (user.get("token").equals(token)) {
                user.replace("socket", socket);
                user.replace("online", 1);
                user.replace("expires_in", System.currentTimeMillis() + (ConfigSingleton.getInstance().getTokenExpires() * 1000));
                return true;
            }
        }
        return false;
    }

    public void autoRevokeToken() {
        for (Map user : users) {
            if (user.get("token") != "") {
                if ((long) user.get("expires_in") < System.currentTimeMillis()) {
                    ServerSingleton.getInstance().log("[SYSTEM] -> User: " + user.get("username") + " token's revoked");
                    user.replace("token", "");
                    user.replace("expires_in", 0);
                }
            }
        }
    }

    public Map getUserObj(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                return user;
            }
        }
        return null;
    }

    public Object getUserToken(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                return user.get("token");
            }
        }
        return "";
    }

    public Object getUserGroup(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                return user.get("group");
            }
        }
        return -1;
    }

    public int getUserId(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                return (int) user.get("id");
            }
        }
        return -1;
    }

    public String getUserName(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                return (String) user.get("username");
            }
        }
        return "";
    }

    public Object getTokenExpires(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                return user.get("expires_in");
            }
        }
        return -1;
    }

    public int getIdByToken(String token) {
        for (Map user : users) {
            if (user.get("token").equals(token)) {
                return (int) user.get("id");
            }
        }
        return -1;
    }

    public void revokUserToken(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                user.replace("token", "");
                user.replace("expires_in", 0);
            }
        }
    }

    public void setUserOffline(String socket) {
        for (Map user : users) {
            if (user.get("socket").equals(socket)) {
                user.replace("online", 0);
            }
        }
    }

    public void removeUser(String socket) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).get("socket").equals(socket)) {
                users.remove(i);
            }
        }
    }
}
