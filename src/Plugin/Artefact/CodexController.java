package Plugin.Artefact;

import Core.Controller;
import Core.Http.Header;
import Core.Http.Map;
import Core.Http.Oauth2;
import Core.Methode;
import Core.Route;
import Plugin.Artefact.Model.CodexModel;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by teddy on 29/05/2016.
 */
@Controller
public class CodexController {
    @Methode("GET")
    @Route("/codex")
    public CodexModel getCodex(String socket, Oauth2 oauth2, Header header, JSONObject jsonObject, Map args) {
        return new CodexModel();
    }
}
