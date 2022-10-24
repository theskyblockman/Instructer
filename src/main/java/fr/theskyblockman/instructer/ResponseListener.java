package fr.theskyblockman.instructer;

/**
 * An interface to represent a listener
 */
public interface ResponseListener {
    /**
     * A method called when a new server have logged to the server
     */
    default void onLogin() {

    }
}

