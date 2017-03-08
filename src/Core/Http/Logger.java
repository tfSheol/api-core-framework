package Core.Http;

import Core.Singleton.UserSecuritySingleton;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by teddy on 28/05/2016.
 */
public class Logger extends Thread {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d-HH:mm:ss");
    private SimpleDateFormat dateFileFormat = new SimpleDateFormat("yyyy-M-d_HH-mm-ss");
    private SimpleDateFormat currentDayFormat = new SimpleDateFormat("yyyy-M-d");
    private CopyOnWriteArrayList<HashMap<String, String>> log = new CopyOnWriteArrayList<>();
    private String currentDay = currentDayFormat.format(System.currentTimeMillis());
    private final static String LOCAL = "LOCAL";
    private final static String ERROR = "ERROR";
    private final static int MAX_REQUEST = Integer.MAX_VALUE / 4;
    private String startValue;
    private long nbLogFiles = 0;
    private int nbRequest = 0;
    private PrintWriter pw;
    private File file;

    @SuppressWarnings("all")
    public Logger() {
        if (!new File("logs").exists()) {
            new File("logs").mkdirs();
        }
        initNewFolderLog();
    }

    @SuppressWarnings("all")
    private void initNewFolderLog() {
        startValue = dateFileFormat.format(System.currentTimeMillis());
        if (!new File("logs/" + startValue).exists()) {
            new File("logs/" + startValue).mkdirs();
            nbLogFiles = 0;
            initNewLoggerFile();
        }
    }

    private void initNewLoggerFile() {
        try {
            if (pw != null) {
                pw.close();
            }
            if (nbLogFiles >= Long.MAX_VALUE - 1000) {
                initNewFolderLog();
            }
            file = new File("logs/" + startValue + "/log_" + startValue + "_" + nbLogFiles++ + ".txt");
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile())), true);
            String currentTime = "[" + dateFormat.format(System.currentTimeMillis()) + "]";
            pw.println(currentTime + "[LOCAL][LOGGER] -> New log file in: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkNbRequest(String currentTime) {
        if (nbRequest++ >= MAX_REQUEST) {
            nbRequest = 0;
            pw.println(currentTime + "[LOCAL][LOGGER] -> End log file & init the next file...");
            initNewLoggerFile();
        }
    }

    public void run() {
        while (true) {
            try {
                if (pw != null && !log.isEmpty()) {
                    for (int i = 0; i < log.size(); i++) {
                        String currentTime = "[" + dateFormat.format(System.currentTimeMillis()) + "]";
                        String user = "";
                        if (log.get(i) != null) {
                            if (log.get(i).containsKey("socket") && !log.get(i).get("socket").equals("[" + LOCAL + "]")) {
                                String tmp = UserSecuritySingleton.getInstance().getUserName(log.get(i).get("socket").replace("[", "").replace("]", ""));
                                if (!tmp.isEmpty()) {
                                    user = "[" + tmp + "]";
                                }
                            }
                            if (log.get(i).containsKey("error")) {
                                pw.println(currentTime + log.get(i).get("socket") + user + log.get(i).get("value") + log.get(i).get("error"));
                                System.err.println(log.get(i).get("socket") + user + log.get(i).get("value") + log.get(i).get("error"));
                            } else {
                                pw.println(currentTime + log.get(i).get("socket") + user + log.get(i).get("value"));
                                System.out.println(log.get(i).get("socket") + user + log.get(i).get("value"));
                            }
                            log.remove(i);
                            checkNbRequest(currentTime);
                            pw.flush();
                            i--;
                        }
                        //Thread.sleep(10);
                    }
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLogMsg(String value) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("socket", "[" + LOCAL + "]");
        hashMap.put("value", value);
        log.add(hashMap);
    }

    public void setLogMsg(String socket, String value) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("socket", "[" + socket + "]");
        hashMap.put("value", value);
        log.add(hashMap);
    }

    public void setLogMsg(String socket, String value, String error) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("error", "[" + ERROR + "]");
        hashMap.put("socket", "[" + socket + "]");
        hashMap.put("value", value);
        hashMap.put("error", error);
        log.add(hashMap);
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay() {
        currentDay = currentDayFormat.format(System.currentTimeMillis());
    }

    public void setNewLog() {
        initNewFolderLog();
    }

    public void closeFile() {
        if (pw != null) {
            String currentTime = "[" + dateFormat.format(System.currentTimeMillis()) + "]";
            pw.println(currentTime + "[LOCAL][LOGGER] -> Shutdown logger system...");
            pw.close();
        }
    }
}
