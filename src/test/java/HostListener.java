import fr.theskyblockman.instructer.response.*;

public class HostListener implements ResponseListener {
    @ResponseHandler(listenedPacket = "ping")
    @RespondTo(interactor = "manager")
    @RespondTo(interactor = "host")
    public Response respondPing(ResponseBuilder builder) {
        return builder.setArgument("ping", "pong").build();
    }
}
