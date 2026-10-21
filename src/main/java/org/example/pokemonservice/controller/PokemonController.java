package org.example.pokemonservice.controller;

import org.example.pokemonservice.api.AllPokemonEntity;
import org.example.pokemonservice.api.PokemonResponse;
import org.example.pokemonservice.dto.ComparisonDTO;
import org.example.pokemonservice.service.PokemonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    @GetMapping
    public List<AllPokemonEntity> getAllPokemon() {
        return service.getAllPokemon();
    }

    @GetMapping("/name/{name}")
    public PokemonResponse getPokemonByName(@PathVariable String name) {
        return service.getPokemonByName(name);
    }

    @GetMapping("/random")
    public PokemonResponse getRandomPokemon() {
        return service.getRandomPokemon();
    }

    @GetMapping("/compare/{name1}/{name2}")
    public ComparisonDTO compare2Pokemon(@PathVariable String name1, @PathVariable String name2) {
        return service.compare2Pokemon(name1, name2);
    }

    @GetMapping("/type/{type}")
    public List<AllPokemonEntity> getPokemonByType(@PathVariable String type) {
        return service.getPokemonByType(type);
    }
}
