package Core.Http;

import Core.Singleton.UserSecuritySingleton;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by teddy on 28/05/2016.
 */
public class Logger extends Thread {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy-HH:mm:ss");
    private SimpleDateFormat dateFileFormat = new SimpleDateFormat("d-M-yyyy_HH-mm-ss");
    private SimpleDateFormat currentDayFormat = new SimpleDateFormat("d-M-yyyy");
    private File file = new File("./logs/log_" + dateFileFormat.format(System.currentTimeMillis()) + ".txt");
    private ArrayList<HashMap<String, String>> log = new ArrayList<>();
    private String currentDay = currentDayFormat.format(System.currentTimeMillis());
    private static String LOCAL = "LOCAL";
    private static String ERROR = "ERROR";
    PrintWriter pw;

    public Logger() {
        try {
            if (!new File("./logs").exists()) {
                new File("logs").mkdirs();
            }
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                if (!log.isEmpty()) {
                    for (int i = 0; i < log.size(); i++) {
                        String currentTime = "[" + dateFormat.format(System.currentTimeMillis()) + "]";
                        String user = "";
                        if (!log.get(i).get("socket").equals("[" + LOCAL + "]")) {
                            String tmp = UserSecuritySingleton.getInstance().getUserName(log.get(i).get("socket").replace("[", "").replace("]", ""));
                            if (!tmp.isEmpty()) {
                                user = "[" + tmp + "]";
                            }
                        }
                        if (log.get(i).containsKey("error")) {
                            pw.println(currentTime + log.get(i).get("error") + log.get(i).get("socket") + user + log.get(i).get("value"));
                            System.err.println(log.get(i).get("error") + log.get(i).get("socket") + user + log.get(i).get("value"));
                        } else {
                            pw.println(currentTime + log.get(i).get("socket") + user + log.get(i).get("value"));
                            System.out.println(log.get(i).get("socket") + user + log.get(i).get("value"));
                        }
                        log.remove(i);
                        i--;
                        Thread.sleep(10);
                    }
                }
                Thread.sleep(50);
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

    public void setLogMsg(String value, boolean error) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("socket", "[" + LOCAL + "]");
        hashMap.put("error", "[" + ERROR + "]");
        hashMap.put("value", value);
        log.add(hashMap);
    }

    public void setLogMsg(String socket, String value) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("socket", "[" + socket + "]");
        hashMap.put("value", value);
        log.add(hashMap);
    }

    public void setLogMsg(String socket, String value, boolean error) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("error", "[" + ERROR + "]");
        hashMap.put("socket", "[" + socket + "]");
        hashMap.put("value", value);
        log.add(hashMap);
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay() {
        currentDay = currentDayFormat.format(System.currentTimeMillis());
    }

    public void setNewLog() {
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
        pw.close();
    }
}
