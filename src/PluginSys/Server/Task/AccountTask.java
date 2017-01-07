package PluginSys.Server.Task;

import Core.Http.Job;
import Core.Singleton.PermsSingleton;
import Core.Singleton.ServerSingleton;
import Core.Singleton.UserSecuritySingleton;
import Core.Task;

/**
 * Created by teddy on 18/06/2016.
 */
@Task(repeat = false)
public class AccountTask extends Job {
    @Override
    public void task() {
        /*UserSecuritySingleton.getInstance().addUser(1, "Sheol", UserSecuritySingleton.hashSHA1("test"), PermsSingleton.MEMBER);
        UserSecuritySingleton.getInstance().addUser(2, "Admin", UserSecuritySingleton.hashSHA1("admin"), PermsSingleton.ADMIN);
        UserSecuritySingleton.getInstance().addUser(3, "Modo", UserSecuritySingleton.hashSHA1("modo"), PermsSingleton.MODO);
        ServerSingleton.getInstance().log("[SYSTEM] -> Nb users loaded: " + UserSecuritySingleton.getInstance().getNbUsers());*/
    }
}
