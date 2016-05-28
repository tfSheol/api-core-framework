package Core.Http;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by teddy on 28/05/2016.
 */
public class Logger extends Thread {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy-HH:mm:ss");
    private SimpleDateFormat dateFileFormat = new SimpleDateFormat("d-M-yyyy_HH-mm-ss");
    private File file = new File("./logs/log_" + dateFileFormat.format(System.currentTimeMillis()) + ".txt");
    private ArrayList<String> log = new ArrayList<>();
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
                        pw.println(dateFormat.format(System.currentTimeMillis()) + " " + log.get(i));
                        System.out.println(log.get(i));
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
        log.add(value);
    }

    public void closeFile() {
        pw.close();
    }
}
