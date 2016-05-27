package Core.Http;

import Core.Model;
import Core.Singleton.UserSecuritySingleton;

/**
 * Created by teddy on 21/05/2016.
 */
public class Oauth2Model extends Model {
    public String access_token;
    public Long expires_in;
    public String token_type = Oauth2.BEARER.toLowerCase();
    public String scope = "read/write";
    public Integer group;

    public Oauth2Model(String socket, Oauth2 oauth2) {
        if (oauth2.login(socket)) {
            access_token = (String) UserSecuritySingleton.getInstance().getUserToken(socket);
            group = (int) UserSecuritySingleton.getInstance().getUserGroup(socket);
            expires_in = (long) UserSecuritySingleton.getInstance().getTokenExpires(socket);
        } else {
            setCode(socket, Code.UNAUTHORIZED);
            scope = null;
            token_type = null;
            expires_in = null;
            group = null;
        }
        data = null;
    }
}
