package org.example.pokemonservice.api;


import java.util.List;

public record PokemonResponse(Integer id,
                              String name,
                              Integer base_experience,
                              Integer height,
                              Integer weight,
                              List<Types> types) {
}
