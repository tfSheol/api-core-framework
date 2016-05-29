package Plugin.Server;

import Core.Controller;
import Core.Http.Oauth2;
import Core.Methode;
import Core.Route;
import Plugin.Server.Model.EmailModel;
import Plugin.Server.Model.OvhSMSModel;
import Plugin.Server.Model.ServerModel;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by teddy on 29/05/2016.
 */
@Controller
public class ServerController {
    @Methode("GET")
    @Route("/server")
    public ServerModel getServer(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new ServerModel();
    }

    @Methode("POST")
    @Route("/server/mail")
    public EmailModel postServerMail(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new EmailModel().send(socket, "[TEST] blabla", "fdjhsqdkjfhqs");
    }

    @Methode("POST")
    @Route("/server/sms")
    public OvhSMSModel postServerSMS(String socket, Oauth2 oauth2, HashMap<String, String> headerField, JSONObject jsonObject, HashMap<String, Object> args) {
        return new OvhSMSModel().sendSms();
    }
}
