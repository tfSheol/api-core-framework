package Core.Http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by teddy on 10/09/2016.
 */
public class Tools {
    public static boolean isValidEmailAddress(String email) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
