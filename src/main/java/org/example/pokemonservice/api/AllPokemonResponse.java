package org.example.pokemonservice.api;

import java.util.List;

public record AllPokemonResponse(Integer count, List<AllPokemonEntity> results) {
}
