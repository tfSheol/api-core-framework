package Core.Http;

import Core.Model;

/**
 * Created by teddy on 04/08/2016.
 */
public class Success extends Model {
    @Override
    protected Object setData(Map result) {
        return null;
    }

    public Success(String socket) {
        setCode(socket, Code.OK);
    }

    public Success msg(String errorMsg) {
        super.setErrorMsg(errorMsg);
        return this;
    }
}
