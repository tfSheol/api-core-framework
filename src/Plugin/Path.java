package Plugin;

import Core.Controller;
import Core.Http.Oauth2;
import Core.Http.Oauth2Model;
import Core.Methode;
import Core.Model;
import Core.Route;
import Core.Singleton.UserSecuritySingleton;
import Plugin.Artefact.Model.Codex;
import Plugin.Server.Model.Server;
import Plugin.Test.Model.TestModel;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by teddy on 04/05/2016.
 */
@Controller
public class Path {
    @Methode("POST")
    @Route("/oauth")
    public Oauth2Model getToken(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new Oauth2Model(socket, oauth2);
    }

    @Methode("DELETE")
    @Route("/revoke")
    public Model revokeToken(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        UserSecuritySingleton.getInstance().revokUserToken(socket);
        return new Model();
    }

    @Methode("GET")
    @Route("/server")
    public Server getServer(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new Server();
    }

    @Methode("GET")
    @Route("/test/{limit}/{id}")
    public TestModel getTest(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        System.out.println("[DEBUG] -> " + args.get("limit") + " " + args.get("id"));
        return new TestModel();
    }

    @Methode("GET")
    @Route("/codex")
    public Codex getCodex(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new Codex();
    }
}
