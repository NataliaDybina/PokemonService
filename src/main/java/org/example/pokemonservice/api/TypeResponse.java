package org.example.pokemonservice.api;

import java.util.List;

public record TypeResponse(Integer id, String name, List<TypePokemonListResponse> pokemon) {
}
