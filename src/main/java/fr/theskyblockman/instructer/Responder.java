package fr.theskyblockman.instructer;

/**
 * An object to retrieve a method to respond to a request from the user
 */
public interface Responder {
    void onResponse(Response response);
}