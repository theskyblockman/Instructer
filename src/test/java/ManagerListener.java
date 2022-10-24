import fr.theskyblockman.instructer.*;

public class ManagerListener implements ResponseListener {
    @ResponseHandler(listenedPacket = PacketType.PING)
    @RespondTo(interactor = ServerType.MANAGER)
    @RespondTo(interactor = ServerType.HOST)
    public Response respondPing(ResponseBuilder builder) {
        return builder.setArgument("ping", "pong").build();
    }
}
