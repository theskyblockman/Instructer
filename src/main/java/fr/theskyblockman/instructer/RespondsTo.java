package fr.theskyblockman.instructer;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation to say what a listener method wants responds to
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RespondsTo {
    /**
     * The values the method is registered for
     * @return the server types the method handles
     */
    RespondTo[] value();
}
