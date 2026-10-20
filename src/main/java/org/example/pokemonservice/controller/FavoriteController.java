package org.example.pokemonservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.example.pokemonservice.dto.FavoriteDTO;
import org.example.pokemonservice.dto.UpdateFavoriteDTO;
import org.example.pokemonservice.model.Favorite;
import org.example.pokemonservice.service.FavoriteService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
@Validated
public class FavoriteController {

    private final FavoriteService service;

    public FavoriteController(FavoriteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Favorite> getAllFavorites() {
        return service.getAllFavorites();
    }

    @GetMapping("/{id}")
    public Favorite getFavoriteById(@PathVariable @NotBlank String id) {
        return service.getFavoriteById(id);
    }

    @PostMapping
    public Favorite addFavorite(@RequestBody @Valid FavoriteDTO favoriteDTO) {
        return service.addFavorite(favoriteDTO);
    }

    @PutMapping("/{id}")
    public Favorite updateFavorite(@PathVariable @NotBlank String id, @RequestBody @Valid UpdateFavoriteDTO updateFavoriteDTO) {
        return service.updateFavorite(id, updateFavoriteDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteFavoriteById(@PathVariable @NotBlank String id) {
        service.deleteFavoriteById(id);
    }

    @GetMapping("/search")
    public List<Favorite> searchFavorites(@RequestParam(required = false) String name,
                                          @RequestParam(required = false) String nickname) {
        return service.searchFavorites(name, nickname);
    }
}
