package Core.Http;

import Core.Singleton.UserSecuritySingleton;

/**
 * Created by teddy on 22/05/2016.
 */
public class Oauth2TokenService extends Thread {
    public void run() {
        while (true) {
            try {
                UserSecuritySingleton.getInstance().autoRevokeToken();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
