package fr.theskyblockman.instructer;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * A representation of a request sent / to send to a server
 */
public class Request implements Serializable, Cloneable {
    /**
     * The type of packet the current request is
     */
    public final PacketType packetType;
    /**
     * The arguments of the request
     */
    public final Map<String, Object> arguments;
    /**
     * The ID of the request
     */
    public UUID id;
    /**
     * The method to call when a response is received (not sent)
     */
    transient Responder responder;

    /**
     * Constructor of a new request
     * @param packetType The type of the request
     * @param arguments The arguments of the request
     * @param responder The method to call when a response is received
     */
    public Request(PacketType packetType, @NotNull Map<String, Object> arguments, Responder responder) {
        this.packetType = packetType;
        arguments.put("type", "request");
        this.arguments = arguments;
        this.id = UUID.randomUUID();
        this.responder = responder;
    }

    /**
     * Serialize the request with GSON to JSON
     * @return the serialized request
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Randomize the UUID to clone a request
     */
    public void randomizeUUID() {
        this.id = UUID.randomUUID();
    }

    /**
     * Creates a clone of the current request
     * @return the clone of the current request (with the same UUID)
     */
    @Override
    public Request clone() {
        try {
            return (Request) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
