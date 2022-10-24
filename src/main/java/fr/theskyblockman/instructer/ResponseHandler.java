package fr.theskyblockman.instructer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark a listener request as able to respond to a specific typ of packet
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseHandler {
    /**
     * The type of packet the method is able to respond to
     * @return The type of the packet the method listens to
     */
    PacketType listenedPacket();

}
