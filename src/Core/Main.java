package Core;

import Core.Singleton.ConfigSingleton;
import Core.Socket.ThreadPool;

/**
 * Created by teddy on 04/05/2016.
 */
public class Main {
    public static void main(String[] args) {
        int port = -1;
        System.out.println("***********************************************************");
        System.out.println("** " + ConfigSingleton.getInstance().getName());
        System.out.println("** Version " + ConfigSingleton.getInstance().getVersion());
        System.out.println("** By " + ConfigSingleton.getInstance().getAuthor());
        System.out.println("***********************************************************");
        try {
            port = ConfigSingleton.getInstance().getPort();
        } catch (NumberFormatException nfe) {
            System.exit(1);
        }
        if (port <= 0 || port > 65536) {
            System.err.println("[SERVER] -> Port value must be in (0, 65535].");
            System.exit(1);
        }
        final ThreadPool server = new ThreadPool(port);
        server.start();
        try {
            server.join();
            System.out.println("[SERVER] -> Completed shutdown.");
        } catch (InterruptedException e) {
            System.err.println("[SERVER] -> Interrupted before accept thread completed.");
            System.exit(1);
        }
    }
}
