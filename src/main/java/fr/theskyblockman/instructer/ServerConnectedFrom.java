package fr.theskyblockman.instructer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A representation of a server connexion started by the other server
 */
public class ServerConnectedFrom {
    /**
     * The socket of the connexion to the other server
     */
    public Socket otherSocket;
    /**
     * The reader to receive requests from the server
     */
    public BufferedReader in;
    /**
     * The printer to send requests to the server
     */
    public PrintWriter out;
    /**
     * The token hash used by the server to connect to the current server
     */
    public String usedHashToConnect;
    /**
     * The position of the other server
     */
    public ServerType otherPosition;
    /**
     * The current server
     */
    public Server currentServer;

    /**
     * The constructor of the server
     * @param loginRequest The request used to log in to the current server
     * @param socket The socket used for the connexion between the two servers
     * @param serverCurrentType The type of the current server
     * @param currentServer The current server
     * @throws IOException If we couldn't connect to the other server
     */
    public ServerConnectedFrom(Request loginRequest, Socket socket, ServerType serverCurrentType, Server currentServer) throws IOException {
        this.otherSocket = socket;
        this.usedHashToConnect = (String) loginRequest.arguments.get("token");
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.otherPosition = ServerType.valueOf((String) loginRequest.arguments.get("position"));
        this.currentServer = currentServer;
        send(new ResponseBuilder(loginRequest, this.otherPosition).setArgument("position", serverCurrentType).build());
    }

    /**
     * The method to send a response to the other server
     * @param response The response to send
     */
    public void send(Response response) {
        this.out.println(response.toJSON());
    }
}
