import fr.theskyblockman.instructer.*;

public class HostListener implements ResponseListener {
    @ResponseHandler(listenedPacket = PacketType.PING)
    @RespondTo(interactor = ServerType.MANAGER)
    @RespondTo(interactor = ServerType.HOST)
    public Response respondPing(ResponseBuilder builder) {
        return builder.setArgument("ping", "pong").build();
    }
}
