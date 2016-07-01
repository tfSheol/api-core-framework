package Core;

import Core.Database.SQLRequest;
import Core.Http.Code;
import Core.Singleton.ServerSingleton;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by teddy on 21/05/2016.
 */
public class Model {
    private String path;
    private String method;
    private int code = Code.OK;
    private String error = "OK";
    private String error_msg;
    private long timestamp = System.currentTimeMillis();
    protected ArrayList<Object> data = new ArrayList<>();
    protected ArrayList<Object> make = new ArrayList<>();
    protected int id = -1;

    public Model() {
        Reflections reflections = new Reflections("Plugin.*");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class<?> obj : annotated) {
            for (Method method : obj.getDeclaredMethods()) {
                if (method.getName().equals(new Exception().getStackTrace()[2].getMethodName())) {
                    this.path = method.getAnnotation(Route.class).value();
                    this.method = method.getAnnotation(Methode.class).value();
                }
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

    public String getErrorMsg() {
        return error_msg;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setErrorMsg(String errorMsg) {
        this.error_msg = errorMsg;
    }

    public void setCode(String socket, int code) {
        if (code == Code.BAD_REQUEST ||
                code == Code.FORBIDDEN ||
                code == Code.INTERNAL_SERVER_ERROR ||
                code == Code.METHOD_NOT_ALLOWED ||
                code == Code.NO_CONTENT ||
                code == Code.NOT_FOUND ||
                code == Code.UNAUTHORIZED) {
        }
        error = capitalizeAllWords(getCodeName(code));
        this.code = code;
        ServerSingleton.getInstance().setHttpCode(socket, code);
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

    protected void setGet(String request) {

    }

    protected void setPost(String socket, String request) {
        SQLRequest sqLite = new SQLRequest(request);
        sqLite.insert();
        id = sqLite.getGeneratedId();
    }

    protected void setPut(String socket, String request) {
        SQLRequest sqLite = new SQLRequest(request);
        sqLite.update();
        id = sqLite.getGeneratedId();
    }

    protected void setDelete(String socket, String request) {
        SQLRequest sqLite = new SQLRequest(request);
        sqLite.delete();
        id = sqLite.getGeneratedId();
    }
}
