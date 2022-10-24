package fr.theskyblockman.instructer;

/**
 * A class to represent some types of servers (all of those are examples excepted for the manager)
 */
@SuppressWarnings("unused")
public enum ServerType {
    /**
     * Where all user data is stored
     */
    DATABASE("database"), // Where all player data is stored
    /**
     * The link between the servers, a way to link users
     */
    LINKER("linker"),
    /**
     * A server where the users get redirected to
     */
    HOST("host"),
    /**
     * A CDN where all resources for servers can be stored
     */
    RESOURCE("resource"),
    /**
     * The server manager
     */
    MANAGER("manager"),
    /**
     * A server for network managers
     */
    ADMIN("admin")
    ;
    /**
     * The name of the server
     */
    public final String name;

    /**
     * The constructor of the server type
     * @param name The server type name
     */
    ServerType(String name) {
        this.name = name;
    }
}
