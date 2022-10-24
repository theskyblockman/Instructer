package fr.theskyblockman.instructer;

import java.util.HashMap;
import java.util.Map;

/**
 * The builder given to end user to create responses
 */
public class ResponseBuilder {
    /**
     * The request received to be used as a draft when the response will be built
     */
    private final Request initialRequest;
    /**
     * The arguments of the built response
     */
    private final Map<String, Object> args = new HashMap<>();
    /**
     * The type of the server of the server this response will be sent to
     */
    private final ServerType otherServerType;

    /**
     * The getter of the initial request
     * @return the initial request
     */
    @SuppressWarnings("unused")
    public Request getInitialRequest() {
        return initialRequest;
    }

    /**
     * The getter of the type of server of the server the response will be sent to
     * @return the other server type
     */
    @SuppressWarnings("unused")
    public ServerType getOtherServerType() {
        return otherServerType;
    }

    /**
     * The constructor of the response builder
     * @param initialRequest The initial request used as a draft to build the response
     * @param otherServerType The type of the other server the response will be sent to
     */
    public ResponseBuilder(Request initialRequest, ServerType otherServerType) {
        this.initialRequest = initialRequest;
        this.otherServerType = otherServerType;
    }

    /**
     * A method to set an argument in a response
     * @param key The argument key
     * @param value The argument value
     * @return The request builder for chain
     */
    public ResponseBuilder setArgument(String key, Object value) {
        args.put(key, value);
        return this;
    }

    /**
     * The method to build the response
     * @return The build response
     */
    public Response build() {
        return new Response(initialRequest.packetType, args, initialRequest.id);
    }
}
