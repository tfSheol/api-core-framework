package Core.Singleton;

import Core.Controller;
import Core.Http.Code;
import Core.Http.Logger;
import Core.Http.LoggerService;
import Core.Task;
import org.reflections.Reflections;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by teddy on 05/05/2016.
 */
public class ServerSingleton {
    private CopyOnWriteArrayList<HashMap<String, Object>> httpRequest = new CopyOnWriteArrayList<>();
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
        createFolder("plugins");
        logger.setLogMsg("[SYSTEM] -> Nb plugins loaded: " + annotated.size());
        taskRunner();
    }

    private void createFolder(String folder) {
        if (!Files.exists(Paths.get(folder))) {
            try {
                Files.createDirectory(Paths.get(folder));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        initPlugins(folder);
    }

    private void initPlugins(String folder) {
        File pluginsDir = new File(System.getProperty("user.dir") + "/" + folder);
        String[] jarFileInFolder = pluginsDir.list((directory, fileName) -> fileName.endsWith(".jar"));
        if (jarFileInFolder != null) {
            for (String jar : jarFileInFolder) {
                try {
                    JarFile jarFile = new JarFile(folder + "/" + jar);
                    Enumeration<JarEntry> enumeration = jarFile.entries();
                    URL[] urls = {new URL("jar:file:" + folder + "/" + jar + "!/")};
                    URLClassLoader loader = URLClassLoader.newInstance(urls);
                    while (enumeration.hasMoreElements()) {
                        JarEntry jarEntry = enumeration.nextElement();
                        if (jarEntry.getName().endsWith(".class")) {
                            String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6).replace('/', '.');
                            if (className.startsWith("Plugin.")) {
                                Class currentClass = loader.loadClass(className);
                                addController(currentClass, className);
                                addTask(currentClass, className);
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addController(Class currentClass, String className) {
        if (currentClass.isAnnotationPresent(Controller.class)) {
            annotated.add(currentClass);
            logger.setLogMsg("[PLUGIN] -> Extra plugin: " + currentClass.getSimpleName().replace("Controller", ""));
        }
    }

    private void addTask(Class currentClass, String className) {
        if (currentClass.isAnnotationPresent(Task.class)) {
            tasks.add(currentClass);
            logger.setLogMsg("[PLUGIN] -> Extra task: " + currentClass.getSimpleName().replace("Task", ""));
        }
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
            if (!httpRequest.get(i).isEmpty() && httpRequest.get(i).get("socket").equals(socket)) {
                httpRequest.remove(i);
            }
        }
    }

    public void log(String string) {
        logger.setLogMsg(string);
    }

    public void log(String string, Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        logger.setLogMsg(string, errors.toString());
    }

    public void log(String socket, String string) {
        logger.setLogMsg(socket, string);
    }

    public void log(String socket, String string, Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        logger.setLogMsg(socket, string, errors.toString());
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
