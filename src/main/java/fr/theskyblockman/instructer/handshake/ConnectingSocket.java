package fr.theskyblockman.instructer.handshake;

import fr.theskyblockman.instructer.ConnectedServer;
import fr.theskyblockman.instructer.Request;
import fr.theskyblockman.instructer.Server;
import fr.theskyblockman.instructer.servers.ServerConnectedFrom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a server being connected
 */
public class ConnectingSocket {
    /**
     * The socket used to connect to the other server
     */
    public Socket socket;
    /**
     * The moment where the socket have been connected
     */
    public Long connexionTimestamp;
    /**
     * All the token hashes waited (under sha256) from the server
     */
    public List<String> awaitedHashes;
    /**
     * The printer where we send requests
     */
    public PrintWriter out;
    /**
     * The reader where we receive requests
     */
    public BufferedReader in;
    /**
     * Verify if a connexion have been tried
     */
    public boolean triedConnexion = false;
    /**
     * Does the tried connexion have been successful
     */
    public boolean connexionSuccessful = false;
    /**
     * The type of the other server
     */
    public String otherServerType;
    /**
     * The Request received when logged in
     */
    public Request loginRequest;
    /**
     * The type of the current server
     */
    private final String currentServerType;
    /**
     * The current server
     */
    public Server currentServer;

    /**
     * The constructor of the representation of a connecting server
     * @param socket the socket needed to create the connexion
     * @param awaitedHashes a list of hash that the server can use to connect himself
     * @param currentServerType the type of the current server
     * @param currentServer the current server
     * @throws IOException if we failed to communicate with the other server
     */
    public ConnectingSocket(Socket socket, List<String> awaitedHashes, String currentServerType, Server currentServer) throws IOException {
        this.socket = socket;
        this.connexionTimestamp = new Date().getTime();
        this.awaitedHashes = awaitedHashes;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.currentServerType = currentServerType;
        this.currentServer = currentServer;
    }

    /**
     * Verify if a log in have been attempted and update the class if yes
     * @throws IOException If the socket connexion have been closed
     */
    public void checkStream() throws IOException {
        String currentLine = in.readLine();

        if(currentLine != null) {

            Request req = Server.gson.fromJson(currentLine, Request.class);
            loginRequest = req;
            otherServerType = (String) req.arguments.get("position");
            if(!Objects.equals(req.packetType, "login")) {
                return;
            }
            triedConnexion = true;
            connexionSuccessful = (awaitedHashes.isEmpty() || awaitedHashes.contains(Server.doSHA256Hash((String) req.arguments.get("token")))) && otherServerType != null;

        }
    }

    /**
     * Transform the connecting socket to a connected server
     * @return The representation of a connected server
     * @throws IOException If the communication with the socket have been ended
     */
    public ConnectedServer toConnected() throws IOException {
        return new ConnectedServer(new ServerConnectedFrom(loginRequest, socket, currentServerType, currentServer));
    }
}
