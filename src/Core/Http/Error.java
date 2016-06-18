package Core.Http;

import Core.Model;

/**
 * Created by teddy on 21/05/2016.
 */
public class Error extends Model {
    @Override
    public void setErrorMsg(String errorMsg) {
        super.setErrorMsg(errorMsg);
    }

    @Override
    public void setMethod(String method) {
        super.setMethod(method);
    }

    @Override
    public void setCode(String socket, int code) {
        super.setCode(socket, code);
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
    }
}