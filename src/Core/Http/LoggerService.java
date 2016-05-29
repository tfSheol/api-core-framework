package Core.Http;

import Core.Singleton.ServerSingleton;

import java.text.SimpleDateFormat;

/**
 * Created by teddy on 29/05/2016.
 */
public class LoggerService extends Thread {
    private SimpleDateFormat currentDayFormat = new SimpleDateFormat("d-M-yyyy");
    private String currentDay;

    public void run() {
        while (true) {
            try {
                currentDay = currentDayFormat.format(System.currentTimeMillis());
                if (!currentDay.equals(ServerSingleton.getInstance().getCurrentDay())) {
                    ServerSingleton.getInstance().setCurrentDay();
                    ServerSingleton.getInstance().closeLogger();
                    ServerSingleton.getInstance().setNewLog();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
