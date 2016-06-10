package Core.Http;

import Core.Task;

/**
 * Created by teddy on 06/06/2016.
 */
public abstract class Job extends Thread {
    public void run() {
        try {
            if (!this.getClass().getAnnotation(Task.class).repeat()) {
                Thread.sleep(this.getClass().getAnnotation(Task.class).value());
                task();
            } else {
                while (true) {
                    Thread.sleep(this.getClass().getAnnotation(Task.class).value());
                    task();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void task();
}
