package Plugin.Server.Model;

import Core.Model;
import Core.Singleton.ConfigSingleton;
import Core.Singleton.NbClientsSingleton;
import Core.Singleton.ServerSingleton;
import Plugin.Server.Obj.ServerObj;

/**
 * Created by teddy on 04/05/2016.
 */
public class ServerModel extends Model {
    private Runtime runtime = Runtime.getRuntime();
    private Integer mb = 1024 * 1024;

    public ServerModel() {
        ServerObj serverObj = new ServerObj();
        serverObj.clients = NbClientsSingleton.getInstance().getNbClients();
        serverObj.ip_host = ServerSingleton.getInstance().getHostIp();
        serverObj.port = ConfigSingleton.getInstance().getPort();
        serverObj.name = ConfigSingleton.getInstance().getName();
        serverObj.version = ConfigSingleton.getInstance().getVersion();
        serverObj.socket_timeout = ConfigSingleton.getInstance().getSocketTimeout();
        serverObj.used_memory = (runtime.totalMemory() - runtime.freeMemory()) / mb;
        serverObj.free_memory = runtime.freeMemory() / mb;
        serverObj.total_available_memory = runtime.totalMemory() / mb;
        serverObj.maximum_available_memory = runtime.maxMemory() / mb;
        data.add(serverObj);
        runtime = null;
        mb = null;
    }
}
