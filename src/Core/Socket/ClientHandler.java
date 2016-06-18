package Core.Socket;

import Core.Http.Header;
import Core.Model;
import Core.Router;
import Core.Singleton.ConfigSingleton;
import Core.Singleton.NbClientsSingleton;
import Core.Singleton.ServerSingleton;
import Core.Singleton.UserSecuritySingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;

/**
 * Created by teddy on 04/05/2016.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSock;
    private static String EOF = "\u0000";
    private String jsonClient = "";
    private Header headerField = new Header();
    private String method = "";
    private String route = "";
    private String protocolVersion = "";

    public ClientHandler(final Socket clientSocket) {
        this.clientSock = clientSocket;
    }

    @Override
    public void run() {
        BufferedReader userInput;
        DataOutputStream userOutput;
        String clientId = clientSock.getRemoteSocketAddress().toString();
        try {
            userInput = new BufferedReader(new InputStreamReader(clientSock.getInputStream(), ConfigSingleton.getInstance().getCharset()));
            userOutput = new DataOutputStream(clientSock.getOutputStream());
            if (!setInitialData(clientId, userInput.readLine())) {
                if (!checkInitialData()) {
                    String buffer;
                    if (!method.equals("OPTIONS")) {
                        while ((buffer = userInput.readLine()).length() > 2) {
                            ServerSingleton.getInstance().log(clientId, "[HEADER] -> " + buffer);
                            headerField.put(buffer.split(": ")[0], buffer.split(": ")[1]);
                        }
                        if ((headerField.containsKey("Accept") && headerField.getString("Accept").equals("application/json")) || (headerField.containsKey("Content-Type") && headerField.getString("Content-Type").equals("application/json"))) {
                            if (headerField.containsKey("Content-Length") && headerField.getInt("Content-Length") > 0) {
                                byte[] array = new byte[0];
                                while ((array.length != headerField.getInt("Content-Length"))) {
                                    jsonClient = jsonClient + (char) userInput.read();
                                    array = jsonClient.getBytes();
                                }
                            }
                            Router router = new Router();
                            ServerSingleton.getInstance().log(clientId, "[USER] -> " + method + " " + route);
                            JSONObject jsonObject = new JSONObject();
                            if (Router.isJSONValid(jsonClient)) {
                                jsonObject = new JSONObject(jsonClient);
                            }
                            String jsonReturn = router.find(clientId, method, route, headerField, jsonObject);
                            userOutput.write(makeResult(clientId, jsonReturn).getBytes(ConfigSingleton.getInstance().getCharset()));
                            userOutput.flush();
                        }
                    } else {
                        ServerSingleton.getInstance().log(clientId, "[USER] -> " + method + " " + route);
                        userOutput.write(makeOptionsResult().getBytes(ConfigSingleton.getInstance().getCharset()));
                        userOutput.flush();
                    }
                }
            }
            userInput.close();
            userOutput.close();
            UserSecuritySingleton.getInstance().setUserOffline(clientId);
            ServerSingleton.getInstance().log("[SERVER] -> Close connection to " + clientId);
            ServerSingleton.getInstance().removeHttpRequest(clientId);
            clientSock.close();
            NbClientsSingleton.getInstance().delClient();
        } catch (IOException | JSONException ioe) {
            ServerSingleton.getInstance().log("IOException : " + ioe, true);
        }
    }

    private boolean setInitialData(String clientId, String data) {
        if (data != null) {
            ServerSingleton.getInstance().log(clientId, "[REQUEST] -> " + data);
            String[] tmp = data.split(" ");
            if (tmp.length == 3) {
                method = tmp[0];
                route = tmp[1];
                protocolVersion = tmp[2];
            }
        } else {
            return true;
        }
        return false;
    }

    private boolean checkInitialData() {
        return route.isEmpty() || (!method.equals("OPTIONS") && !method.equals("POST") && !method.equals("GET") && !method.equals("PUT") && !method.equals("DELETE")) || !protocolVersion.equals("HTTP/1.1");
    }

    private String makeResult(String clientId, String json) throws UnsupportedEncodingException {
        final byte[] utf8Bytes = json.getBytes(ConfigSingleton.getInstance().getCharset());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String currentDate = dateFormat.format(System.currentTimeMillis());
        int code = (int) ServerSingleton.getInstance().getHttpCode(clientId);
        return "HTTP/1.1 " + code + " " + Model.getCodeName(code) + "\r\n" +
                "Date: " + currentDate + "\r\n" +
                "Server: " + ConfigSingleton.getInstance().getName() + "/" + ConfigSingleton.getInstance().getVersion() + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Access-Control-Allow-Origin: " + ConfigSingleton.getInstance().getPropertie("Access-Control-Allow-Origin") + "\r\n" +
                "Access-Control-Allow-Credentials: true\r\n" +
                "Access-Control-Allow-Headers: origin, content-type, accept, Authorization\r\n" +
                "Access-Control-Allow-Methods: OPTIONS, GET, PUT, POST, DELETE\r\n" +
                "Content-Length: " + utf8Bytes.length + "\r\n" +
                "Expires: " + currentDate + "\r\n" +
                "Last-modified: " + currentDate + "\r\n" +
                "\r\n" + json + EOF;
    }

    private String makeOptionsResult() {
        return "HTTP/1.1 200 OK\r\n" +
                "Access-Control-Allow-Origin: " + ConfigSingleton.getInstance().getPropertie("Access-Control-Allow-Origin") + "\r\n" +
                "Access-Control-Allow-Credentials: true\r\n" +
                "Access-Control-Allow-Headers: origin, content-type, accept, Authorization\r\n" +
                "Access-Control-Allow-Methods: OPTIONS, GET, PUT, POST, DELETE\r\n"
                + "\r\n" + EOF;
    }
}
