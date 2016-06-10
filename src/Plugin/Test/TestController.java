package Plugin.Test;

import Core.Controller;
import Core.Http.Header;
import Core.Http.Map;
import Core.Http.Oauth2;
import Core.Methode;
import Core.Route;
import Core.Singleton.ServerSingleton;
import Plugin.Test.Model.TestModel;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by teddy on 29/05/2016.
 */
@Controller
public class TestController {
    @Methode("GET")
    @Route("/test/{limit}/{id}")
    public TestModel getTest(String socket, Oauth2 oauth2, Header header, JSONObject jsonObject, Map args) {
        ServerSingleton.getInstance().log(socket, "[DEBUG] -> " + args.getInt("limit") + " " + args.getInt("id"));
        return new TestModel();
    }
}
