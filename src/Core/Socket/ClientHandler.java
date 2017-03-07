package Core.Socket;

import Core.Http.Code;
import Core.Http.Header;
import Core.Model;
import Core.Router;
import Core.Singleton.*;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.zip.GZIPOutputStream;

/**
 * Created by teddy on 04/05/2016.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSock;
    private static String EOF = "\r\n";
    private String method = "";
    private String route = "";
    private String protocolVersion = "";
    private BufferedReader userInput;
    private DataOutputStream userOutput;
    private int nbRequestKeepAlive = ConfigSingleton.getInstance().getInt("keep-alive-max");

    public ClientHandler(final Socket clientSocket) {
        this.clientSock = clientSocket;
    }

    @Override
    @SuppressWarnings("all")
    public void run() {
        String clientId = clientSock.getRemoteSocketAddress().toString();
        boolean keepConnect = true;
        try {
            clientSock.setSoTimeout(ConfigSingleton.getInstance().getInt("keep-alive-timeout") * 1000);
            try {
                String tmp;
                userInput = new BufferedReader(new InputStreamReader(clientSock.getInputStream(), ConfigSingleton.getInstance().getCharset()));
                userOutput = new DataOutputStream(clientSock.getOutputStream());
                while (keepConnect && (tmp = userInput.readLine()) != null
                        && !tmp.equals("") && tmp.length() > 0 && !IpSingleton.getInstance().isBanned(IpSingleton.getInstance().convertToIp(clientId))) {
                    keepConnect = ConfigSingleton.getInstance().getBoolean("keep-alive");
                    if (keepConnect) {
                        clearData();
                    }
                    String buffer;
                    String jsonClient = "";
                    ServerSingleton.getInstance().setHttpCode(clientId, Code.OK);
                    Header headerField = new Header();
                    if (!setInitialData(clientId, tmp)) {
                        if (!checkInitialData()) {
                            while ((buffer = userInput.readLine()).length() > 2) {
                                if (!buffer.contains("Basic")) {
                                    ServerSingleton.getInstance().log(clientId, "[HEADER] -> " + buffer);
                                }
                                headerField.put(buffer.split(": ")[0].toLowerCase(), buffer.split(": ")[1]);
                            }
                            if (!method.equals("OPTIONS")) {
                                nbRequestKeepAlive--;
                                if (nbRequestKeepAlive <= 0) {
                                    keepConnect = false;
                                }
                                if ((headerField.containsKey("accept") && headerField.getString("accept").equals("application/json")) || (headerField.containsKey("content-type") && headerField.getString("content-type").equals("application/json"))) {
                                    clientSock.setSoTimeout(ConfigSingleton.getInstance().getInt("keep-alive-timeout") * 1000);
                                    if (headerField.containsKey("content-length") && headerField.getInt("content-length") > 0) {
                                        byte[] array = new byte[0];
                                        while ((array.length != headerField.getInt("content-length"))) {
                                            jsonClient = jsonClient + (char) userInput.read();
                                            array = jsonClient.getBytes();
                                        }
                                    }
                                    Router router = new Router();
                                    ServerSingleton.getInstance().log(clientId, "[USER] -> " + method + " " + route);
                                    JSONObject jsonObject = new JSONObject();
                                    if (Router.isJSONValid(jsonClient)) {
                                        jsonObject = new JSONObject(jsonClient);
                                        if (!jsonClient.contains("password")) {
                                            ServerSingleton.getInstance().log(clientId, "[USER] -> " + jsonClient);
                                        }
                                    }
                                    String jsonReturn = router.find(clientId, method, route, headerField, jsonObject);
                                    userOutput.write(makeResult(clientId, jsonReturn));
                                    userOutput.flush();
                                } else {
                                    IpSingleton.getInstance().setIpFail(clientId);
                                }
                            } else {
                                ServerSingleton.getInstance().log(clientId, "[USER] -> " + method + " " + route);
                                userOutput.write(makeOptionsResult().getBytes(ConfigSingleton.getInstance().getCharset()));
                                userOutput.flush();
                            }
                        } else {
                            IpSingleton.getInstance().setIpFail(clientId);
                            close(clientId);
                            break;
                        }
                    }
                    tmp = null;
                }
            } catch (SocketTimeoutException e) {
                close(clientId);
            } finally {
                if (!keepConnect) {
                    close(clientId);
                }
            }
        } catch (SocketException e) {
            ServerSingleton.getInstance().log("[SERVER] -> Connection lost to " + clientId);
        } catch (Exception e) {
            ServerSingleton.getInstance().log("IOException", e);
        }
    }

    private void clearData() {
        method = "";
        route = "";
    }

    private void close(String clientId) {
        try {
            userInput.close();
            userOutput.close();
            clientSock.close();
            UserSecuritySingleton.getInstance().setUserOffline(clientId);
            ServerSingleton.getInstance().log("[SERVER] -> Close connection to " + clientId);
            ServerSingleton.getInstance().removeHttpRequest(clientId);
            NbClientsSingleton.getInstance().delClient();
        } catch (IOException e) {
            ServerSingleton.getInstance().log("SocketTimeoutException: ", e);
        }
    }

    private byte[] compress(String str) throws Exception {
        ServerSingleton.getInstance().log("[SERVER GZIP] -> Current length " + str.length());
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes(ConfigSingleton.getInstance().getCharset()));
        gzip.close();
        byte[] outStr = obj.toByteArray();
        ServerSingleton.getInstance().log("[SERVER GZIP] -> Encoded length " + outStr.length);
        return outStr;
    }

    private boolean setInitialData(String clientId, String data) {
        if (data != null) {
            String[] tmp = data.split(" ");
            if (tmp.length == 3) {
                method = tmp[0];
                route = tmp[1];
                protocolVersion = tmp[2];
                ServerSingleton.getInstance().log(clientId, "[REQUEST] -> " + data);
            }
        } else {
            return true;
        }
        return false;
    }

    private boolean checkInitialData() {
        return route.isEmpty() || (!method.equals("OPTIONS") && !method.equals("POST") && !method.equals("GET") && !method.equals("PUT") && !method.equals("DELETE")) || !protocolVersion.equals("HTTP/1.1");
    }

    private byte[] makeResult(String clientId, String json) throws Exception {
        byte[] encodedJSON = compress(json);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss Z");
        String currentDate = dateFormat.format(System.currentTimeMillis());
        String expireDate = dateFormat.format(System.currentTimeMillis() + ConfigSingleton.getInstance().getInt("expire") * 1000);
        int code = (int) ServerSingleton.getInstance().getHttpCode(clientId);
        byte[] data = ("HTTP/1.1 " + code + " " + Model.getCodeName(code) + "\r\n" +
                "Date: " + currentDate + "\r\n" +
                "Server: " + ConfigSingleton.getInstance().getName() + "/" + ConfigSingleton.getInstance().getVersion() + "\r\n" +
                "Access-Control-Allow-Origin: " + ConfigSingleton.getInstance().getString("Access-Control-Allow-Origin") + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Connection: keep-alive\r\n" +
                "Keep-Alive: max=" + nbRequestKeepAlive + ", timeout=" + ConfigSingleton.getInstance().getInt("keep-alive-timeout") + "\r\n" +
                "Content-Encoding: gzip\r\n" +
                "Content-Length: " + encodedJSON.length + "\r\n" +
                "Expires: " + expireDate + "\r\n" +
                "Last-modified: " + currentDate + "\r\n" +
                "\r\n").getBytes(ConfigSingleton.getInstance().getCharset());
        byte[] ret = new byte[data.length + encodedJSON.length];
        System.arraycopy(data, 0, ret, 0, data.length);
        System.arraycopy(encodedJSON, 0, ret, data.length, encodedJSON.length);
        byte[] byteEOF = EOF.getBytes(ConfigSingleton.getInstance().getCharset());
        byte[] withEOF = new byte[ret.length + byteEOF.length];
        System.arraycopy(ret, 0, withEOF, 0, ret.length);
        System.arraycopy(byteEOF, 0, withEOF, ret.length, byteEOF.length);
        return withEOF;
    }

    private String makeOptionsResult() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss Z");
        String currentDate = dateFormat.format(System.currentTimeMillis());
        return "HTTP/1.1 200 OK\r\n" +
                "Date: " + currentDate + "\r\n" +
                "Server: " + ConfigSingleton.getInstance().getName() + "/" + ConfigSingleton.getInstance().getVersion() + "\r\n" +
                "Access-Control-Allow-Origin: " + ConfigSingleton.getInstance().getString("Access-Control-Allow-Origin") + "\r\n" +
                "Connection: keep-alive\r\n" +
                "Keep-Alive: max=" + nbRequestKeepAlive + ", timeout=" + ConfigSingleton.getInstance().getInt("keep-alive-timeout") + "\r\n" +
                "Content-Length: 0\r\n" +
                "Accept-Encoding: gzip\r\n" +
                "Allow: OPTIONS, GET, PUT, POST, DELETE\r\n" +
                "Access-Control-Allow-Credentials: true\r\n" +
                "Access-Control-Allow-Headers: origin, content-type, accept, authorization\r\n" +
                "Access-Control-Allow-Methods: OPTIONS, GET, PUT, POST, DELETE\r\n" +
                EOF;
    }
}
