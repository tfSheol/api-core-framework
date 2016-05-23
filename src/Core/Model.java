package Core;

import Core.Http.Code;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by teddy on 21/05/2016.
 */
public class Model {
    private String path;
    private String method;
    private int code = Code.OK;
    private String error = "OK";
    private long timestamp = System.currentTimeMillis();
    protected ArrayList<Object> data = new ArrayList<>();

    public Model() {
        Class<Plugin.Path> obj = Plugin.Path.class;
        for (Method method : obj.getDeclaredMethods()) {
            if (method.getName().equals(new Exception().getStackTrace()[2].getMethodName())) {
                this.path = method.getAnnotation(Route.class).value();
                this.method = method.getAnnotation(Methode.class).value();
            }
        }
    }

    public ArrayList<Object> getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public int getCode() {
        return code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setCode(int code) {
        if (code == Code.BAD_REQUEST ||
                code == Code.FORBIDDEN ||
                code == Code.INTERNAL_SERVER_ERROR ||
                code == Code.METHOD_NOT_ALLOWED ||
                code == Code.NO_CONTENT ||
                code == Code.NOT_FOUND ||
                code == Code.UNAUTHORIZED) {
            data.clear();
            data = null;
        }
        error = capitalizeAllWords(getCodeName(code));
        this.code = code;
    }

    public static String capitalizeAllWords(String str) {
        String[] ret = str.toLowerCase().split(" ");
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Character.toUpperCase(ret[i].charAt(0)) + ret[i].substring(1);
        }
        return String.join(" ", ret);
    }

    public static String getCodeName(int code) {
        for (int i = 0; i < Code.class.getDeclaredFields().length; i++) {
            try {
                if (Code.class.getDeclaredFields()[i].getInt(Integer.class) == code) {
                    return Code.class.getDeclaredFields()[i].getName().replaceAll("_", " ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "OK";
    }
}
