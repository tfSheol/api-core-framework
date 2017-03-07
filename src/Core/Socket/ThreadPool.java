package Core.Socket;

import Core.Http.Oauth2TokenService;
import Core.Singleton.*;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by teddy on 04/05/2016.
 */
public class ThreadPool extends Thread {
    private final ExecutorService workers = Executors.newCachedThreadPool();
    private ServerSocket listenSocket;
    private volatile boolean keepRunning = true;

    @SuppressWarnings("all")
    public ThreadPool(final int port) {
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadPool.this::shutdown));
        try {
            ConfigSingleton.getInstance();
            NbClientsSingleton.getInstance();
            PermsSingleton.getInstance();
            UserSecuritySingleton.getInstance();
            new Oauth2TokenService().start();
            if (ConfigSingleton.getInstance().isSSL()) {
                System.setProperty("javax.net.ssl.keyStore", "ssl/keystore.jks");
                System.setProperty("javax.net.ssl.keyStorePassword", "test1234");
                System.setProperty("javax.net.ssl.keyStoreType", "JKS");
                System.setProperty("javax.net.ssl.trustStore", "ssl/chain.jks");
                System.setProperty("javax.net.ssl.trustStorePassword", "test1234");
                System.setProperty("javax.net.ssl.trustStoreType", "JKS");
                System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
                SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                listenSocket = (SSLServerSocket) socketFactory.createServerSocket(port);
            } else {
                listenSocket = new ServerSocket(port);
            }
            ServerSingleton.getInstance().setHostIp(String.valueOf(listenSocket.getLocalSocketAddress()));
        } catch (IOException e) {
            ServerSingleton.getInstance().log("[SERVER] -> An exception occurred while creating the listen socket: ", e);
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            listenSocket.setSoTimeout(ConfigSingleton.getInstance().getSocketTimeout());
        } catch (SocketException e) {
            ServerSingleton.getInstance().log("[SERVER] -> Unable to set acceptor timeout value. The server may not shutdown gracefully.", e);
        }
        ServerSingleton.getInstance().log("[SERVER] -> Accepting incoming connections on port " + listenSocket.getLocalPort());
        while (keepRunning) {
            try {
                final Socket clientSocket = listenSocket.accept();
                if (!IpSingleton.getInstance().isBanned(IpSingleton.getInstance().convertToIp(clientSocket.getRemoteSocketAddress().toString()))) {
                    ServerSingleton.getInstance().log("[SERVER] -> Accepted connection from " + clientSocket.getRemoteSocketAddress());
                    ServerSingleton.getInstance().addHttpRequest(clientSocket.getRemoteSocketAddress().toString());
                    NbClientsSingleton.getInstance().addClient();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    workers.execute(handler);
                } else {
                    clientSocket.close();
                }
            } catch (SocketTimeoutException te) {
                System.err.print("");
            } catch (IOException e) {
                ServerSingleton.getInstance().log("[SERVER] -> Exception occurred while handling client request: ", e);
                Thread.yield();
            }
        }
        try {
            listenSocket.close();
        } catch (IOException e) {
            ServerSingleton.getInstance().log("[SERVER] -> IOException: ", e);
        }
        ServerSingleton.getInstance().log("[SERVER] -> Stopped accepting incoming connections.");
    }

    private void shutdown() {
        ServerSingleton.getInstance().log("[SERVER] -> Shutting down the server.");
        ServerSingleton.getInstance().closeLogger();
        NbClientsSingleton.getInstance().razClient();
        keepRunning = false;
        workers.shutdownNow();
        try {
            join();
        } catch (InterruptedException e) {
            ServerSingleton.getInstance().log("[SERVER] -> Shutdown: ", e);
        }
    }
}
