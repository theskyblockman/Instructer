package fr.theskyblockman.instructer;

/**
 * All the types of packet that can be sent.
 */
@SuppressWarnings("unused")
public enum PacketType {
    /**
     * The login packet, if needed you can send it with the "token" argument if the manager needs a token
     */
    LOGIN("login"),
    /**
     * The logout packet, sent when the connection to a trusted server is finished
     */
    LOGOUT("logout"),
    /**
     * The ping packet, sends a test packet witch shouldn't be normally used excepted for tests
     */
    PING("ping"),
    /**
     * The register packet, sent to register a token to another server witch expires after 5 seconds and is one time use
     */
    REGISTER("register"),
    /**
     * The query packet, query a MySQL line to the database server
     */
    QUERY("query"),
    /**
     * The server requester packet, sent to manager, gives one time usable tokens with server ips and ports to log in to all servers
     */
    GET_ASSIGNED_SERVERS("get_servers"),
    /**
     * The server health packet, asks for any server all it's statistics (player amount, CPU, RAM, run time, version, server UUIDs...) this can be also used to do a full server map to know how to quickly link servers
     */
    GET_SERVER_HEALTH("get_server_health"),
    /**
     * The server change type packet, sent by manager, request another server to change its type to fit to the demand
     */
    CHANGE_TYPE("change_type")
    ;
    /**
     * The name of the packet
     */
    public final String name;
    PacketType(String name) {
        this.name = name;
    }
}
