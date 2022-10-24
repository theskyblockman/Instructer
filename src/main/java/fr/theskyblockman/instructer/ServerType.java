package fr.theskyblockman.instructer;

/**
 * A class to represent all the types of servers
 */
@SuppressWarnings("unused")
public enum ServerType {
    /**
     * Where all player data is stored
     */
    DATABASE("database"), // Where all player data is stored
    /**
     * The link between the servers, a way to link players
     */
    LINKER("linker"), // The link between the servers, a way to link players
    /**
     * A server where the players get redirected to
     */
    HOST("host"),
    /**
     * A CDN where all resources for servers are stored (maps, libs, resource packs...)
     */
    RESOURCE("resource"),
    /**
     * The "boss" who manages everything
     */
    MANAGER("manager"),
    /**
     * The admin GUI to manage every server manually
     */
    ADMIN("admin")
    // API("api") API server???
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
