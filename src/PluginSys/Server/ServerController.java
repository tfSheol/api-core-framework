package PluginSys.Server;

import Core.Controller;
import Core.Http.Header;
import Core.Http.Map;
import Core.Http.Oauth2;
import Core.Methode;
import Core.Route;
import PluginSys.Server.Model.ServerModel;
import org.json.JSONObject;

/**
 * Created by teddy on 29/05/2016.
 */
@Controller
public class ServerController {
    @Methode("GET")
    @Route("/server")
    public ServerModel getServer(String socket, Oauth2 oauth2, Header header, JSONObject jsonObject, Map args) {
        return new ServerModel();
    }
}
