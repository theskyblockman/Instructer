package fr.theskyblockman.instructer.servers;

import fr.theskyblockman.instructer.Server;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.ConnectIOException;

/**
 * A representation of a server connexion started by the current server
 */
public class ServerConnectedTo {
    /**
     * The socket of the connexion to the other server
     */
    public Socket socket;

    /**
     * The reader to receive requests from the server
     */
    public BufferedReader in;
    /**
     * The printer to send requests to the server
     */
    public PrintWriter out;
    /**
     * The position of the other server
     */
    public String otherPosition;
    /**
     * The current server
     */
    public Server currentServer;
    /**
     * Is the current server logged to the other server
     */
    public boolean logged = false;
    /**
     * The token used to connect to the other server
     */
    public final String token;

    /**
     * The position of the current server
     */
    public final String currentPosition;

    /**
     * The constructor of the server connexion
     * @param serverIP The IP of the server
     * @param serverPort The port of the server
     * @param token The token to connect with to the server
     * @param currentPosition The current position of the server
     * @param currentServer The current server
     * @throws ConnectIOException if we cannot establish connexion
     */
    public ServerConnectedTo(String serverIP, int serverPort, @Nullable String token, String currentPosition, Server currentServer) throws ConnectIOException {
        try {
            this.currentServer = currentServer;
            this.socket = new Socket(serverIP, serverPort);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.currentPosition = currentPosition;
            this.token = token;

        } catch (IOException e) {
            throw new ConnectIOException("Could not connect to server with ip " + serverIP + " and port " + serverPort + "!");
        }
    }
}
