package Plugin.Server;

import Core.Controller;
import Core.Http.Header;
import Core.Http.Map;
import Core.Http.Oauth2;
import Core.Http.Oauth2Model;
import Core.Methode;
import Core.Route;
import org.json.JSONObject;

/**
 * Created by teddy on 29/05/2016.
 */
@Controller
public class Oauth2Controller {
    @Methode("POST")
    @Route("/oauth")
    public Oauth2Model getToken(String socket, Oauth2 oauth2, Header header, JSONObject jsonObject, Map args) {
        return new Oauth2Model().initUser(socket, oauth2);
    }

    @Methode("DELETE")
    @Route("/revoke")
    public Oauth2Model revokeToken(String socket, Oauth2 oauth2, Header header, JSONObject jsonObject, Map args) {
        return new Oauth2Model().revokToken(socket);
    }
}
