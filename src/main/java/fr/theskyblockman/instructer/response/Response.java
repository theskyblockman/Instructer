package fr.theskyblockman.instructer.response;

import fr.theskyblockman.instructer.PacketType;
import fr.theskyblockman.instructer.Server;

import java.util.Map;
import java.util.UUID;

/**
 * The representation of a response
 */
public class Response {
    /**
     * The type of packet witch the current response is
     */
    public final String packetType;
    /**
     * The arguments of the response
     */
    public final Map<String, Object> responseArgs;
    /**
     * The ID of the response
     */
    public final UUID ID;

    /**
     * The constructor of a response
     * @param packetType The type of packet the response will be
     * @param responseArgs The arguments of the response
     * @param requestID The ID of the request to put in the response
     */
    @SuppressWarnings("unused")
    public Response(PacketType packetType, Map<String, Object> responseArgs, UUID requestID) {
        this(packetType.name, responseArgs, requestID);
    }
    public Response(String packetType, Map<String, Object> responseArgs, UUID requestID) {
        this.packetType = packetType;
        responseArgs.put("type", "response");
        this.responseArgs = responseArgs;
        this.ID = requestID;
    }

    /**
     * Serialises with GSON the request to JSON
     * @return the serialized response
     */
    public String toJSON() {
        return Server.gson.toJson(this);
    }
}
