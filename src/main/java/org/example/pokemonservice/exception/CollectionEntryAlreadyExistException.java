package org.example.pokemonservice.exception;

public class CollectionEntryAlreadyExistException extends RuntimeException {
    public CollectionEntryAlreadyExistException(String message) {
        super(message);
    }
}
