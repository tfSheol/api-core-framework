package Core.Singleton;

import Core.Controller;
import Core.Http.Code;
import Core.Http.Logger;
import Core.Http.LoggerService;
import Core.Task;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by teddy on 05/05/2016.
 */
public class ServerSingleton {
    private ArrayList<HashMap<String, Object>> httpRequest = new ArrayList<>();
    private String hostIp;
    private Logger logger = new Logger();
    private Set<Class<?>> annotated;
    private Set<Class<?>> tasks;
    private int nbTasks = 0;

    private ServerSingleton() {
        logger.start();
        new LoggerService().start();
        Reflections reflections = new Reflections("Plugin.*");
        annotated = reflections.getTypesAnnotatedWith(Controller.class);
        tasks = reflections.getTypesAnnotatedWith(Task.class);
        taskRunner();
    }

    private static class SingletonHolder {
        private final static ServerSingleton instance = new ServerSingleton();
    }

    public static ServerSingleton getInstance() {
        return SingletonHolder.instance;
    }

    private void taskRunner() {
        for (Class<?> task : tasks) {
            try {
                if (task.getGenericSuperclass().getTypeName().equals("Core.Http.Job")) {
                    Thread thread = (Thread) task.newInstance();
                    thread.start();
                    nbTasks++;
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        log("[SYSTEM] -> Nb tasks loaded: " + nbTasks);
    }

    public Set<Class<?>> getAnnotated() {
        return annotated;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void addHttpRequest(String socket) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("socket", socket);
        request.put("http_code", Code.OK);
        httpRequest.add(request);
    }


    public void setHttpCode(String socket, int code) {
        for (HashMap<String, Object> request : httpRequest) {
            if (request.get("socket").equals(socket)) {
                request.replace("http_code", code);
            }
        }
    }

    public Object getHttpCode(String socket) {
        for (HashMap<String, Object> request : httpRequest) {
            if (request.get("socket").equals(socket)) {
                return request.get("http_code");
            }
        }
        return -1;
    }

    public void removeHttpRequest(String socket) {
        for (int i = 0; i < httpRequest.size(); i++) {
            if (httpRequest.get(i).get("socket").equals(socket)) {
                httpRequest.remove(i);
            }
        }
    }

    public void log(String string) {
        logger.setLogMsg(string);
    }

    public void log(String string, boolean error) {
        logger.setLogMsg(string, error);
    }

    public void log(String socket, String string) {
        logger.setLogMsg(socket, string);
    }

    public void log(String socket, String string, boolean error) {
        logger.setLogMsg(socket, string, error);
    }

    public String getCurrentDay() {
        return logger.getCurrentDay();
    }

    public void setCurrentDay() {
        logger.setCurrentDay();
    }

    public void setNewLog() {
        logger.setNewLog();
    }

    public void closeLogger() {
        logger.closeFile();
    }
}
