package Core.Socket;

import Core.Http.Oauth2TokenService;
import Core.Singleton.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by teddy on 04/05/2016.
 */
public class ThreadPool extends Thread {
    private final ExecutorService workers = Executors.newCachedThreadPool();
    private ServerSocket listenSocket;
    private volatile boolean keepRunning = true;

    public ThreadPool(final int port) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ThreadPool.this.shutdown();
            }
        });

        try {
            listenSocket = new ServerSocket(port);
            ServerSingleton.getInstance().setHostIp(String.valueOf(listenSocket.getLocalSocketAddress()));
            ConfigSingleton.getInstance();
            NbClientsSingleton.getInstance();
            PermsSingleton.getInstance();
            UserSecuritySingleton.getInstance();
            new Thread(new Oauth2TokenService()).start();
        } catch (IOException e) {
            System.err.println("[SERVER] -> An exception occurred while creating the listen socket: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            listenSocket.setSoTimeout(ConfigSingleton.getInstance().getSocketTimeout());
        } catch (SocketException e1) {
            System.err.println("[SERVER] -> Unable to set acceptor timeout value. The server may not shutdown gracefully.");
        }
        System.out.println("[SERVER] -> Accepting incoming connections on port " + listenSocket.getLocalPort());
        while (keepRunning) {
            try {
                final Socket clientSocket = listenSocket.accept();
                System.out.println("[SERVER] -> Accepted connection from " + clientSocket.getRemoteSocketAddress());
                ServerSingleton.getInstance().addHttpRequest(clientSocket.getRemoteSocketAddress().toString());
                NbClientsSingleton.getInstance().addClient();
                ClientHandler handler = new ClientHandler(clientSocket);
                workers.execute(handler);
            } catch (SocketTimeoutException te) {
                System.err.print("");
            } catch (IOException ioe) {
                System.err.println("[SERVER] -> Exception occurred while handling client request: " + ioe.getMessage());
                Thread.yield();
            }
        }
        try {
            listenSocket.close();
        } catch (IOException ioe) {
            System.err.println("[SERVER] -> IOException : " + ioe);
        }
        System.out.println("[SERVER] -> Stopped accepting incoming connections.");
    }

    public void shutdown() {
        System.out.println("[SERVER] -> Shutting down the server.");
        NbClientsSingleton.getInstance().razClient();
        keepRunning = false;
        workers.shutdownNow();
        try {
            join();
        } catch (InterruptedException e) {
            System.err.println("[SERVER] -> Shutdown : " + e);
        }
    }
}
