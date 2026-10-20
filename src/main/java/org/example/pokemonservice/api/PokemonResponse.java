package org.example.pokemonservice.api;


public record PokemonResponse(Integer id,
                              String name,
                              Integer base_experience,
                              Integer height,
                              Integer weight) {
}
