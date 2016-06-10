package Plugin.Server;

import Core.Controller;
import Core.Http.Header;
import Core.Http.Map;
import Core.Http.Oauth2;
import Core.Http.Oauth2Model;
import Core.Methode;
import Core.Model;
import Core.Route;
import Core.Singleton.UserSecuritySingleton;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by teddy on 29/05/2016.
 */
@Controller
public class Oauth2Controller {
    @Methode("POST")
    @Route("/oauth")
    public Oauth2Model getToken(String socket, Oauth2 oauth2, Header header, JSONObject jsonObject, Map args) {
        return new Oauth2Model(socket, oauth2);
    }

    @Methode("DELETE")
    @Route("/revoke")
    public Model revokeToken(String socket, Oauth2 oauth2, Header header, JSONObject jsonObject, Map args) {
        UserSecuritySingleton.getInstance().revokUserToken(socket);
        return new Model();
    }
}
