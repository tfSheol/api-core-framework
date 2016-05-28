package Plugin;

import Core.Controller;
import Core.Http.Oauth2;
import Core.Http.Oauth2Model;
import Core.Methode;
import Core.Model;
import Core.Route;
import Core.Singleton.ServerSingleton;
import Core.Singleton.UserSecuritySingleton;
import Plugin.Artefact.Codex;
import Plugin.Server.Email;
import Plugin.Server.OvhSMS;
import Plugin.Server.Server;
import Plugin.Test.TestModel;
import org.json.JSONObject;

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

    @Methode("POST")
    @Route("/server/mail")
    public Email postServerMail(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new Email().send(socket, "[TEST] blabla", "fdjhsqdkjfhqs");
    }

    @Methode("POST")
    @Route("/server/sms")
    public OvhSMS postServerSMS(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new OvhSMS().sendSms();
    }

    @Methode("GET")
    @Route("/test/{limit}/{id}")
    public TestModel getTest(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        ServerSingleton.getInstance().log(socket, "[DEBUG] -> " + args.get("limit") + " " + args.get("id"));
        return new TestModel();
    }

    @Methode("GET")
    @Route("/codex")
    public Codex getCodex(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new Codex();
    }
}
