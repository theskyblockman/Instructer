import fr.theskyblockman.instructer.*;
import org.junit.Test;
import java.io.IOException;
import java.util.HashMap;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class ServerTests {
    public static boolean successTest = false;
    @Test
    public void testServers() throws IOException {
        Server hostServer = new Server(ServerType.HOST, "none", 8888);
        hostServer.listeners.add(new HostListener());
        Server managerServer = new Server(new String[0], 8080);
        managerServer.listeners.add(new ManagerListener());
        ConnectedServer connected = hostServer.createConnexion("127.0.0.1", 8080);

        connected.send(new Request(PacketType.PING, new HashMap<>(), (response) -> {
                System.out.println("Received response from tests 1");
                managerServer.broadcastRequest(new Request(PacketType.PING, new HashMap<>(), (response1) -> {
                        hostServer.stop();
                        managerServer.stop();
                        System.out.println("Received response from tests 2");
                        assertTrue(response1.responseArgs.containsValue("pong"));
                        successTest = true;
                }), ServerType.HOST);
        }));
        await().until(() -> successTest);
    }
}
