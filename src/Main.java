import Core.Singleton.ConfigSingleton;
import Core.Singleton.ServerSingleton;
import Core.Socket.ThreadPool;

/**
 * Created by teddy on 04/05/2016.
 */
public class Main {
    public static void main(String[] args) {
        int port = -1;
        try {
            port = ConfigSingleton.getInstance().getPort();
        } catch (NumberFormatException nfe) {
            System.exit(1);
        }
        if (port <= 0 || port > 65536) {
            ServerSingleton.getInstance().log("[SERVER] -> Port value must be in (0, 65535].", true);
            System.exit(1);
        }
        final ThreadPool server = new ThreadPool(port);
        server.start();
        try {
            server.join();
            ServerSingleton.getInstance().log("[SERVER] -> Completed shutdown.");
        } catch (InterruptedException e) {
            ServerSingleton.getInstance().log("[SERVER] -> Interrupted before accept thread completed.", true);
            System.exit(1);
        }
    }
}
