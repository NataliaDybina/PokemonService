package org.example.pokemonservice.exception;

public class CollectionEntryNotFoundException extends RuntimeException {
    public CollectionEntryNotFoundException(String message) {
        super(message);
    }
}
