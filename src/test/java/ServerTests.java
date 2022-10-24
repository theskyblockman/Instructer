import fr.theskyblockman.instructer.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTests {
    public boolean successTest = false;
    public Logger logger = Logger.getLogger(ServerTests.class.getName());
    @Test
    public void testServers() throws IOException {
        Server hostServer = new Server("host", "none", 8888);
        hostServer.listeners.add(new HostListener());
        Server managerServer = new Server(new String[0], 8080);
        managerServer.listeners.add(new ManagerListener());
        ConnectedServer connected = hostServer.createConnexion("127.0.0.1", 8080);

        connected.send(new Request(PacketType.PING, new HashMap<>(), (response) -> {
            logger.info("Received response from manager server");
            managerServer.broadcastRequest(new Request(PacketType.PING, new HashMap<>(), (response1) -> {
                logger.info("Received response from host server");
                hostServer.stop();
                managerServer.stop();
                assertTrue(response1.responseArgs.containsValue("pong"));
                successTest = true;
            }), "host");
        }));
        await().until(() -> successTest);
    }
}
