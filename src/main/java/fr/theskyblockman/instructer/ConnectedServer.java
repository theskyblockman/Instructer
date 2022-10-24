package fr.theskyblockman.instructer;

import fr.theskyblockman.instructer.response.Response;
import fr.theskyblockman.instructer.servers.ServerConnectedFrom;
import fr.theskyblockman.instructer.servers.ServerConnectedTo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.awaitility.Awaitility.await;

/**
 * Represents a connected server that can receive and send packets
 */
public class ConnectedServer {
    /**
     * The socket used to communicate with the server
     */
    public Socket socket;
    /**
     * The printer to send packets to the server
     */
    public PrintWriter out;
    /**
     * The reader to read packets received from the server
     */
    public BufferedReader in;
    /**
     * All the requests sent with this server representation, keeping them to use their responder
     */
    public final Map<UUID, Request> requestMap = new HashMap<>();
    /**
     * The server type of the other server
     */
    public String otherPosition;
    /**
     * The current server the packets are being sent by
     */
    public Server currentServer;
    /**
     * If the current server is logged (should be always true)
     */
    public boolean logged;
    /**
     * The current position of the server
     */
    public String currentPosition;
    /**
     * The token used to connect to the server if the server connected himself to the other server
     */
    private String token;

    /**
     * The constructor to transform a server that was connected by the current server
     * @param loggedServer The server to transform
     */
    public ConnectedServer(ServerConnectedTo loggedServer) {
        this.socket = loggedServer.socket;
        this.out = loggedServer.out;
        this.in = loggedServer.in;
        this.otherPosition = loggedServer.otherPosition;
        this.currentServer = loggedServer.currentServer;
        this.logged = loggedServer.logged; // Should be true
        this.currentPosition = loggedServer.currentServer.position;
        this.token = loggedServer.token;
    }

    /**
     * The constructor to transform a server the was connected to us by the other server
     * @param loggedServer The server to transform
     */
    public ConnectedServer(ServerConnectedFrom loggedServer) {
        this.socket = loggedServer.otherSocket;
        this.out = loggedServer.out;
        this.in = loggedServer.in;
        this.otherPosition = loggedServer.otherPosition;
        this.currentServer = loggedServer.currentServer;
        this.logged = true;
        this.currentPosition = loggedServer.currentServer.position;
    }

    /**
     * Send a request
     * @param request the request
     */
    public void send(Request request) {
        currentServer.responseMap.put(request.id, request.responder);
        this.out.println(request.toJSON());
        this.requestMap.put(request.id, request);
    }

    /**
     * Send a response
     * @param response the response
     */
    public void send(Response response) {
        this.out.println(response.toJSON());
    }

    /**
     * Close the connexion between the current server and the other server represented by this class
     */
    public void closeConnexion() {
        send(new Request(PacketType.LOGOUT, new HashMap<>(), response -> {
            try {
                socket.close();
            } catch (IOException ignore) {}
        }));
    }

    /**
     * Connect you to the server, should not be used if the other server connected to us or if we were already connected
     */
    public void login() {
        if(logged) return;
        Map<String, Object> args = new HashMap<>();
        if(token != null) {
            args.put("token", token);
        }
        args.put("position", currentPosition);
        send(new Request(PacketType.LOGIN, args, response -> {
            otherPosition = (String) response.responseArgs.get("position");
            logged = true;
        }));
        await().until(() -> logged);
    }
}
