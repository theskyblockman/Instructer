package fr.theskyblockman.instructer;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import fr.theskyblockman.instructer.handshake.ConnectingSocket;
import fr.theskyblockman.instructer.response.*;
import fr.theskyblockman.instructer.servers.ServerConnectedTo;

import java.io.IOException;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.ConnectIOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * A class to represent the current server
 */
public class Server {
    /**
     * The position of the current server
     */
    public final String position;
    /**
     * The token of the current server to log in to it
     */
    protected final String token;
    /**
     * The token hashes that can be used to log in to the current server
     */
    protected String[] validTokens;

    /**
     * All the active connexions to the server
     */
    private final Map<String, List<ConnectedServer>> connexions = new HashMap<>();
    /**
     * Is the server online or not
     */
    public boolean online = true;
    /**
     * All the registered packet listeners
     */
    public List<ResponseListener> listeners = new ArrayList<>();
    /**
     * The instance of the GSON serializer
     */
    public static final Gson gson = new Gson();

    /**
     * A Map where all responses are kept to be called later
     */
    public final Map<UUID, Responder> responseMap = new HashMap<>();
    /**
     * The socket of the current server
     */
    public ServerSocket socket;
    /**
     * All the server that are currently been connected
     */
    public List<ConnectingSocket> beingConnectedSockets = new ArrayList<>();

    public Logger logger = Logger.getLogger(Server.class.getName());

    /**
     * Verifies if a listener method is callable for a request
     * @param method The method that is being tested
     * @param request The request to test for the method
     * @param otherPosition The position of the sender of the request
     * @return If the method should be called or not
     */
    private boolean isCallable(Method method, Request request, String otherPosition) {
        ResponseHandler handler = method.getAnnotation(ResponseHandler.class);
        RespondsTo responders = method.getAnnotation(RespondsTo.class);
        RespondTo responder = method.getAnnotation(RespondTo.class);
        RespondToEveryBody canRespondToEverybody = method.getAnnotation(RespondToEveryBody.class);
        if(handler == null) {
            return false;
        }
        if(canRespondToEverybody != null) return true;
        if((method.getParameterTypes().length != 1 || method.getParameterTypes()[0] != ResponseBuilder.class || !method.getReturnType().equals(Response.class) || (responders == null && responder == null))) {
            throw new WrongMethodTypeException("A method is incorrect in one of your Listeners, there should be only 1 parameter, a ResponseBuilder that returns a response of the same type of the request and one or more RespondTo annotation");
        }
        if(!handler.listenedPacket().equals(request.packetType)) return false;
        List<String> respondersOfMethod = new ArrayList<>();

        if(responders != null) {
            for (RespondTo respondTo : responders.value()) {
                respondersOfMethod.add(respondTo.interactor());
            }
        } else {
            respondersOfMethod.add(responder.interactor());
        }
        for (String possibleResponse : respondersOfMethod) {
            if(possibleResponse.equals(otherPosition)) return true;
        }
        return false;
    }

    /**
     * The method to create the asynchronous threads for the server, all this code is to receive new connexions (the second thread), to approve connexions and receive requests from the approved connexions (the first thread)
     */
    private void startListeningThread() {
        Server currentServer = this;
        Thread receiverThread = new Thread("Network Receiver") {
            @Override
            public void run() {
                while (online) {
                    List<ConnectingSocket> copyBeingConnectedSockets = new ArrayList<>(beingConnectedSockets);
                    for (ConnectingSocket waitingSocket : copyBeingConnectedSockets) {
                        try {
                            if(!waitingSocket.triedConnexion) {
                                waitingSocket.checkStream();
                                if(waitingSocket.triedConnexion) {
                                    if(waitingSocket.connexionSuccessful) {
                                        logger.info("Server connexion accepted, type: " + waitingSocket.otherServerType + ", timestamp: " + waitingSocket.connexionTimestamp + ", remote ip: " + (((InetSocketAddress) waitingSocket.socket.getRemoteSocketAddress()).getAddress()).toString().replace("/",""));
                                        beingConnectedSockets.remove(waitingSocket);
                                        connexions.putIfAbsent(waitingSocket.otherServerType, new ArrayList<>());
                                        connexions.get(waitingSocket.otherServerType).add(waitingSocket.toConnected());
                                        for (ResponseListener listener : listeners) {
                                            listener.onLogin();
                                        }
                                    } else {
                                        logger.info("Server connexion refused, timestamp: " + waitingSocket.connexionTimestamp);
                                        waitingSocket.socket.close();
                                        beingConnectedSockets.remove(waitingSocket);
                                    }
                                }
                            } else {
                                String currentLine = waitingSocket.in.readLine();
                                if(currentLine != null) {
                                    Response res = gson.fromJson(currentLine, Response.class);
                                    responseMap.get(res.ID).onResponse(res);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Map<String, List<ConnectedServer>> copyConnexion = new HashMap<>(connexions);
                    for (Map.Entry<String, List<ConnectedServer>> connexionType : copyConnexion.entrySet()) {
                        for (ConnectedServer connexion : connexionType.getValue()) {
                            try {
                                String lastReadLine = connexion.in.readLine();

                                if(lastReadLine != null) {
                                    if(isRequest(lastReadLine)) {
                                        Request req = gson.fromJson(lastReadLine, Request.class);
                                        boolean sentRequest = false;
                                        for (ResponseListener listener : listeners) {
                                            for (Method eventMethod : listener.getClass().getDeclaredMethods()) {
                                                if(isCallable(eventMethod, req, connexion.otherPosition)) {
                                                    Response res = (Response) eventMethod.invoke(listener, new ResponseBuilder(req, connexion.otherPosition));
                                                    connexion.send(res);
                                                    sentRequest = true;
                                                    break;
                                                }
                                            }
                                            if(sentRequest) break;
                                        }
                                        if(!sentRequest) {
                                            logger.warning("A method not handled by any listener have been received, it's type is: " + req.packetType);
                                        }
                                    } else {
                                        Response resp = gson.fromJson(lastReadLine, Response.class);
                                        responseMap.get(resp.ID).onResponse(resp);
                                        connexion.requestMap.remove(resp.ID);
                                    }

                                }
                            } catch (IOException ignore) {} catch (InvocationTargetException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        };

        Thread connexionListener = new Thread("Login listener") {
            @Override
            public void run() {
                while(online) {
                    try {
                        Socket receivedSocket = socket.accept();
                        beingConnectedSockets.add(new ConnectingSocket(receivedSocket, Arrays.stream(validTokens).toList(), position, currentServer));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        receiverThread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        connexionListener.start();
        receiverThread.start();
    }

    /**
     * Create a server as a database, a linker, a host or a resource server
     * @param position The position of the server (can not be a manager)
     * @param token The token used to connect to the manager server
     * @param port The port that the server will use
     * @throws IOException If we cannot contact the internet
     */
    public Server(String position, String token, int port) throws IOException {
        this.position = position;
        this.token = token;
        this.socket = new ServerSocket(port);
        startListeningThread();
    }
    /**
     * Create a server as a database, a linker, a host or a resource server
     * @param position The position of the server (can not be a manager)
     * @param token The token used to connect to the manager server
     * @param port The port that the server will use
     * @param validTokens The correct tokens used by other servers to connect to the current server
     * @throws IOException If we cannot contact the internet
     */
    @SuppressWarnings("unused")
    public Server(String position, String token, String[] validTokens, int port) throws IOException {
        this.position = position;
        this.token = token;
        this.socket = new ServerSocket(port);
        this.validTokens = validTokens;
        startListeningThread();
    }

    /**
     * Hashes a specified input
     * @param input The input to hash
     * @return The hashed input
     */
    public static String doSHA256Hash(String input) {
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    /**
     * Verifies if a string is a request
     * @param request the string to test
     * @return The verdict of if the string is a request
     */
    public static boolean isRequest(String request) {
        Request result = gson.fromJson(request, Request.class);
        return result != null && result.arguments != null && result.arguments.get("type").equals("request");
    }

    /**
     * Create a server as a manager
     * @param validTokens the list of token hashes (under MD5 specifications) that can be used by the server
     * @param port The port the server will use
     * @throws IOException if we can't communicate with the internet
     */
    public Server(String[] validTokens, int port) throws IOException {
        if(validTokens.length == 0) {
            logger.warning("There isn't any valid tokens registered in the manager server, the authentication is deactivated");
        }
        this.position = "manager";
        this.validTokens = validTokens;
        this.token = "self";
        this.socket = new ServerSocket(port);
        startListeningThread();
    }

    /**
     * Send a request to a connexion
     * @param request The request to send
     * @param connexion The connexion to send the request to
     */
    @SuppressWarnings("unused")
    public void sendRequest(Request request, ConnectedServer connexion) {
        responseMap.put(request.id, request.responder);
        connexion.send(request);
    }

    /**
     * Broadcasts a request to a type of server
     * @param request The request to broadcast
     * @param serverType The type of server that will receive the request
     */
    public void broadcastRequest(Request request, String serverType) {
        if(!connexions.containsKey(serverType)) return;
        broadcastRequest(request, connexions.get(serverType).toArray(new ConnectedServer[0]));
    }

    /**
     * Broadcasts a request to one or more server
     * @param request The request to broadcast
     * @param connexions All the servers that will receive the request
     */
    public void broadcastRequest(Request request, ConnectedServer...connexions) {
        for (ConnectedServer connexion : connexions) {
            Request clonedRequest = request.clone();
            clonedRequest.randomizeUUID();
            connexion.send(clonedRequest);
        }
    }

    /**
     * Creates a connexion between two servers
     * @param IP The IP of the other server
     * @param port The port of the other server
     * @return The connected server (null if connexion didn't succeed)
     */
    public ConnectedServer createConnexion(String IP, int port) {
        try {
            ConnectedServer connexion = new ConnectedServer(new ServerConnectedTo(IP, port, token, position, this));
            connexions.putIfAbsent(connexion.otherPosition, new ArrayList<>());
            connexions.get(connexion.otherPosition).add(connexion);
            connexion.login();
            return connexion;
        } catch (ConnectIOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the server on demand
     */
    public void stop() {
        logger.info("Stopped " + position + " server.");
        for (List<ConnectedServer> connexionList : connexions.values()) {
            for (ConnectedServer connexion : connexionList) {
                connexion.closeConnexion();
                try {
                    connexion.socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        online = false;
    }
}