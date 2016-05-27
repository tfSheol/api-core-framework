package Plugin.Server;

import Core.Model;
import Core.Singleton.UserSecuritySingleton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class OvhSMS extends Model {
    private String AK = "Nz5fHXudcHrms83b";
    private String AS = "u9HpdrTrH9B7b4yb4hpKGhyA4N5mAFSO";
    private String CK = "vpV2pf0dMioB9RRo3CKxhtAnJk1FupQP";

    public OvhSMS sendSms() {
        String ServiceName = getSmsAccount();
        String METHOD = "POST";
        try {
            URL QUERY = new URL("https://eu.api.ovh.com/1.0/sms/" + ServiceName + "/jobs/");
            String BODY = "{\"receivers\":[\"+33663694367\"],\"message\":\"Test SMS OVH\",\"priority\":\"high\",\"senderForResponse\":true}";
            long TSTAMP = new Date().getTime() / 1000;

            String toSign = AS + "+" + CK + "+" + METHOD + "+" + QUERY + "+" + BODY + "+" + TSTAMP;
            String signature = "$1$" + UserSecuritySingleton.hashSHA1(toSign);
            System.out.println(signature);

            HttpURLConnection req = (HttpURLConnection) QUERY.openConnection();
            req.setRequestMethod(METHOD);
            req.setRequestProperty("Content-Type", "application/json");
            req.setRequestProperty("X-Ovh-Application", AK);
            req.setRequestProperty("X-Ovh-Consumer", CK);
            req.setRequestProperty("X-Ovh-Signature", signature);
            req.setRequestProperty("X-Ovh-Timestamp", "" + TSTAMP);

            if (!BODY.isEmpty()) {
                req.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(req.getOutputStream());
                wr.writeBytes(BODY);
                wr.flush();
                wr.close();
            }

            String inputLine;
            BufferedReader in;
            int responseCode = req.getResponseCode();
            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(req.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(req.getErrorStream()));
            }
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            data.add(response);
        } catch (MalformedURLException e) {
            final String errmsg = "MalformedURLException: " + e;
        } catch (IOException e) {
            final String errmsg = "IOException: " + e;
        }
        return this;
    }

    private String getSmsAccount() {
        String METHOD = "GET";
        try {
            URL QUERY = new URL("https://eu.api.ovh.com/1.0/sms/");
            String BODY = "";

            long TSTAMP = new Date().getTime() / 1000;

            String toSign = AS + "+" + CK + "+" + METHOD + "+" + QUERY + "+" + BODY + "+" + TSTAMP;
            String signature = "$1$" + UserSecuritySingleton.hashSHA1(toSign);

            HttpURLConnection req = (HttpURLConnection) QUERY.openConnection();
            req.setRequestMethod(METHOD);
            req.setRequestProperty("Content-Type", "application/json");
            req.setRequestProperty("X-Ovh-Application", AK);
            req.setRequestProperty("X-Ovh-Consumer", CK);
            req.setRequestProperty("X-Ovh-Signature", signature);
            req.setRequestProperty("X-Ovh-Timestamp", "" + TSTAMP);

            String inputLine;
            BufferedReader in;
            int responseCode = req.getResponseCode();
            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(req.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(req.getErrorStream()));
            }
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString().replace("\"]", "").replace("[\"", "");

        } catch (MalformedURLException e) {
            final String errmsg = "MalformedURLException: " + e;
        } catch (IOException e) {
            final String errmsg = "IOException: " + e;
        }
        return "";
    }
}