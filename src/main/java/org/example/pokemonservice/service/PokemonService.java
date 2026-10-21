package org.example.pokemonservice.service;

import org.example.pokemonservice.api.*;
import org.example.pokemonservice.dto.ComparisonDTO;
import org.example.pokemonservice.exception.PokemonNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PokemonService {

    private final RestClient restClient;

    public PokemonService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://pokeapi.co/api/v2/").build();
    }

    public List<AllPokemonEntity> getAllPokemon() {
        return Objects.requireNonNull(restClient.get().uri("/pokemon").retrieve().body(AllPokemonResponse.class)).results();
    }

    public PokemonResponse getPokemonByName(String name) {
        return restClient.get().uri("/pokemon/{name}", name)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new PokemonNotFoundException("Pokemon with name " + name + " not Found");
                }))
                .body(PokemonResponse.class);
    }

    public PokemonResponse getRandomPokemon() {
        AllPokemonResponse allPokemonResponse = Objects.requireNonNull(
                restClient.get()
                        .uri("/pokemon?limit=100000&offset=0")
                        .retrieve()
                        .body(AllPokemonResponse.class));

        PokemonResponse pokemonResponse = null;
        while (pokemonResponse == null) {
            int randomNumber = ThreadLocalRandom.current().nextInt(1, allPokemonResponse.count() + 1);
            String name = allPokemonResponse.results().get(randomNumber - 1).name();
            try {
                pokemonResponse = getPokemonByName(name);
            } catch (HttpStatusCodeException e) {
                System.out.println("Pokemon with id " + randomNumber + " not found");
            }
        }
        return pokemonResponse;
    }

    public ComparisonDTO compare2Pokemon(String name1, String name2) {
        PokemonResponse pokemonResponse1 = getPokemonByName(name1);
        PokemonResponse pokemonResponse2 = getPokemonByName(name2);
        if (pokemonResponse1 == null) {
            throw new PokemonNotFoundException("Pokemon with name " + name1 + " not found");
        }
        if (pokemonResponse2 == null) {
            throw new PokemonNotFoundException("Pokemon with name " + name2 + " not found");
        }
        String havierPokemon;
        Integer weightDifference = Math.abs(pokemonResponse1.weight() - pokemonResponse2.weight());
        int i = pokemonResponse1.weight().compareTo(pokemonResponse2.weight());
        if (i > 0) {
            havierPokemon = pokemonResponse1.name();
        } else if (i < 0) {
            havierPokemon = pokemonResponse2.name();
        } else {
            havierPokemon = "They're equal";
        }
        String higherPokemon;
        Integer heightDifference = Math.abs(pokemonResponse1.height() - pokemonResponse2.height());
        int j = pokemonResponse1.height().compareTo(pokemonResponse2.height());
        if (j > 0) {
            higherPokemon = pokemonResponse1.name();
        } else if (j < 0) {
            higherPokemon = pokemonResponse2.name();
        } else {
            higherPokemon = "They're equal";
        }
        return new ComparisonDTO(havierPokemon, weightDifference, higherPokemon, heightDifference);
    }

    public List<AllPokemonEntity> getPokemonByType(String type) {
        return Objects.requireNonNull(restClient.get().uri("/type/{type}", type).retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                            throw new IllegalArgumentException("Type " + type + " not Found");
                        }))
                        .body(TypeResponse.class))
                .pokemon()
                .stream()
                .map(TypePokemonListResponse::pokemon)
                .toList();
    }
}
