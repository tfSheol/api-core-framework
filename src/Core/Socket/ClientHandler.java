package Core.Socket;

import Core.Model;
import Core.Router;
import Core.Singleton.ConfigSingleton;
import Core.Singleton.NbClientsSingleton;
import Core.Singleton.ServerSingleton;
import Core.Singleton.UserSecuritySingleton;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by teddy on 04/05/2016.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSock;
    private static String EOF = "\u0000";
    private String jsonClient = "";
    private HashMap<String, String> headerField = new HashMap<>();
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
            userInput = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            userOutput = new DataOutputStream(clientSock.getOutputStream());
            setInitialData(userInput.readLine());
            if (!checkInitialData()) {
                String buffer;
                if (!method.equals("OPTIONS")) {
                    while ((buffer = userInput.readLine()).length() > 2) {
                        headerField.put(buffer.split(": ")[0], buffer.split(": ")[1]);
                    }
                    if (headerField.containsKey("Content-Type") && headerField.get("Content-Type").equals("application/json")) {
                        if (headerField.containsKey("Content-Length")) {
                            while ((jsonClient.length() != Integer.parseInt(headerField.get("Content-Length")))) {
                                jsonClient = jsonClient + (char) userInput.read();
                            }
                        }
                        Router router = new Router();
                        ServerSingleton.getInstance().log(clientId, "[USER] -> " + clientId + " " + method + " " + route);
                        JSONObject jsonObject = new JSONObject();
                        if (Router.isJSONValid(jsonClient)) {
                            jsonObject = new JSONObject(jsonClient);
                        }
                        String jsonReturn = router.find(clientId, method, route, headerField, jsonObject);
                        userOutput.write(makeResult(clientId, jsonReturn).getBytes("UTF8"));
                        userOutput.flush();
                    }
                } else {
                    userOutput.write(makeOptionsResult().getBytes("UTF8"));
                    userOutput.flush();
                }
            }
            userInput.close();
            userOutput.close();
            UserSecuritySingleton.getInstance().setUserOffline(clientId);
            ServerSingleton.getInstance().log(clientId, "[SERVER] -> Close connection to " + clientId);
            ServerSingleton.getInstance().removeHttpRequest(clientId);
            clientSock.close();
            NbClientsSingleton.getInstance().delClient();
        } catch (IOException ioe) {
            System.err.print("IOException : " + ioe);
        }
    }

    private void setInitialData(String data) {
        String[] tmp = data.split(" ");
        method = tmp[0];
        route = tmp[1];
        protocolVersion = tmp[2];
    }

    private boolean checkInitialData() {
        return route.isEmpty() || (!method.equals("OPTIONS") && !method.equals("POST") && !method.equals("GET") && !method.equals("PUT") && !method.equals("DELETE")) || !protocolVersion.equals("HTTP/1.1");
    }

    private String makeResult(String clientId, String json) throws UnsupportedEncodingException {
        final byte[] utf8Bytes = json.getBytes("UTF-8");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String currentDate = dateFormat.format(System.currentTimeMillis());
        int code = (int) ServerSingleton.getInstance().getHttpCode(clientId);
        return "HTTP/1.1 " + code + " " + Model.getCodeName(code) + "\r\n" +
                "Date: " + currentDate + "\r\n" +
                "Server: " + ConfigSingleton.getInstance().getName() + "/" + ConfigSingleton.getInstance().getVersion() + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + utf8Bytes.length + "\r\n" +
                "Expires: " + currentDate + "\r\n" +
                "Last-modified: " + currentDate + "\r\n" +
                "\r\n" + json + EOF;
    }

    private String makeOptionsResult() {
        return "HTTP/1.1 200 OK\n Allow: GET, PUT, POST, DELETE";
    }
}
