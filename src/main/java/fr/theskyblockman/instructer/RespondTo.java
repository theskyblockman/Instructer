package fr.theskyblockman.instructer;

import java.lang.annotation.*;

/**
 * An annotation to mark a method to what server types the method wants to respond to
 */
@Repeatable(RespondsTo.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface RespondTo {
    /**
     * The server type the method requests
     * @return the server type the method requests
     */
    ServerType interactor();
}
