package Core.Singleton;

import Core.Http.Map;
import Core.Http.Oauth2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by teddy on 05/05/2016.
 */
public class UserSecuritySingleton {
    private static UserSecuritySingleton instance = new UserSecuritySingleton();
    private CopyOnWriteArrayList<Map> users = new CopyOnWriteArrayList<>();
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
        for (byte aData : data) {
            int halfbyte = (aData >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = aData & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public CopyOnWriteArrayList<Map> getUsers() {
        return users;
    }

    public int getNbUsers() {
        return nbUsers;
    }

    public void addUser(int id, String username, String password, int group) {
        Map user = new Map();
        user.put("username", username.toLowerCase());
        user.put("password", password);
        user.put("id", id);
        user.put("group", group);
        user.put("online", 0);
        user.put("token_lib", new ArrayList<Map>());
        nbUsers++;
        users.add(user);
    }

    public void cleanUsers() {
        users.clear();
    }

    public void updateUser(String socket, String key, Object value) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    user.replace(key, value);
                }
            }
        }
    }

    public void updateUserById(int id, String key, Object value) {
        for (Map user : users) {
            if (user.containsKey("id") && user.get("id").equals(id)) {
                user.replace(key, value);
            }
        }
    }

    public void updateSocketToken(String socket, String token) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("token") && currentToken.get("token").equals(token)) {
                    currentToken.replace("socket", socket);
                }
            }
        }
    }


    public void updateFullUser(String socket, String username, String password) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    user.replace("username", username);
                    user.replace("password", password);
                }
            }
        }
    }

    public boolean checkUser(String socket, String username, String password) {
        for (Map user : users) {
            if (user.get("username").equals(username) && user.get("password").equals(hashSHA1(password))) {
                ArrayList<Map> currentTokenLib = user.getArrayList("token_lib");
                if (currentTokenLib.size() >= ConfigSingleton.getInstance().getInt("token_max")) {
                    currentTokenLib.remove(0);
                }
                user.replace("online", 1);
                Map map = new Map();
                map.put("socket", socket);
                map.put("token", Oauth2.generateToken());
                map.put("expires_in", System.currentTimeMillis() + (ConfigSingleton.getInstance().getTokenExpires() * 1000));
                currentTokenLib.add(map);
                return true;
            }
        }
        return false;
    }

    public boolean checkToken(String socket, String token) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("token") && currentToken.getString("token").equals(token)) {
                    currentToken.replace("socket", socket);
                    user.replace("online", 1);
                    currentToken.replace("expires_in", System.currentTimeMillis() + (ConfigSingleton.getInstance().getTokenExpires() * 1000));
                    return true;
                }
            }
        }
        return false;
    }

    public void autoRevokeToken() {
        for (Map user : users) {
            for (int i = 0; i < user.getArrayList("token_lib").size(); i++) {
                Map currentToken = user.getArrayList("token_lib").get(i);
                if (currentToken.containsKey("token")) {
                    if ((long) currentToken.get("expires_in") < System.currentTimeMillis()) {
                        ServerSingleton.getInstance().log("[SYSTEM] -> User: " + user.get("username") + " " + currentToken.getString("token") + " token's revoked");
                        user.getArrayList("token_lib").remove(i);
                    }
                }
            }
        }
    }

    public Map getUserObj(String socket) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    return user;
                }
            }
        }
        return null;
    }

    public String getUserToken(String socket) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    return currentToken.getString("token");
                }
            }
        }
        return "";
    }

    public int getUserGroup(String socket) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    return user.getInt("group");
                }
            }
        }
        return -1;
    }

    public int getUserId(String socket) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    return user.getInt("id");
                }
            }
        }
        return -1;
    }

    public String getUserName(String socket) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    return user.getString("username");
                }
            }
        }
        return "";
    }

    public long getTokenExpires(String socket) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    return currentToken.getLong("expires_in");
                }
            }
        }
        return -1;
    }

    public int getIdByToken(String token) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("token").equals(token)) {
                    return user.getInt("id");
                }
            }
        }
        return -1;
    }


    public String getSocketById(int id) {
        for (Map user : users) {
            if (user.containsKey("id") && user.get("id").equals(id)) {
                for (Map currentToken : user.getArrayList("token_lib")) {
                    if (currentToken.containsKey("socket")) {
                        return user.getString("socket");
                    }
                }
            }
        }
        return "";
    }

    public void revokUserToken(String socket) {
        for (Map user : users) {
            for (int i = 0; i < user.getArrayList("token_lib").size(); i++) {
                Map currentToken = user.getArrayList("token_lib").get(i);
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    user.getArrayList("token_lib").remove(i);
                }
            }
        }
    }

    public void setUserOffline(String socket) {
        for (Map user : users) {
            for (Map currentToken : user.getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    user.replace("online", 0);
                }
            }
        }
    }

    public void removeUser(String socket) {
        for (int i = 0; i < users.size(); i++) {
            for (Map currentToken : users.get(i).getArrayList("token_lib")) {
                if (currentToken.containsKey("socket") && currentToken.get("socket").equals(socket)) {
                    users.remove(i);
                }
            }
        }
    }
}
