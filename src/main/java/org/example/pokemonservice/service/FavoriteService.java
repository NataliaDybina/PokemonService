package org.example.pokemonservice.service;

import org.example.pokemonservice.api.PokemonResponse;
import org.example.pokemonservice.dto.FavoriteDTO;
import org.example.pokemonservice.dto.UpdateFavoriteDTO;
import org.example.pokemonservice.exception.CollectionEntryAlreadyExistException;
import org.example.pokemonservice.exception.CollectionEntryNotFoundException;
import org.example.pokemonservice.model.Favorite;
import org.example.pokemonservice.repository.FavoriteRepo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class FavoriteService {

    private final FavoriteRepo favoriteRepo;
    private final PokemonService pokemonService;

    public FavoriteService(FavoriteRepo favoriteRepo, PokemonService pokemonService) {
        this.favoriteRepo = favoriteRepo;
        this.pokemonService = pokemonService;
    }

    public List<Favorite> getAllFavorites() {
        return favoriteRepo.findAll();
    }

    public Favorite getFavoriteById(String id) {
        Favorite favorite = favoriteRepo.findById(id).orElse(null);
        if (Objects.isNull(favorite)) {
            throw new CollectionEntryNotFoundException("Favorite Pokemon with id: " + id + " not found");
        }
        return favorite;
    }

    public Favorite addFavorite(FavoriteDTO favoriteDTO) {
        if (!favoriteRepo.existsByName(favoriteDTO.name())) {
            PokemonResponse pokemonResponse = pokemonService.getPokemonByName(favoriteDTO.name());
            if (Objects.nonNull(pokemonResponse)) {
                Favorite favorite = new Favorite(null,
                        pokemonResponse.name(),
                        favoriteDTO.nickname(),
                        pokemonResponse.id(),
                        pokemonResponse.base_experience(),
                        pokemonResponse.height(),
                        pokemonResponse.weight());
                favoriteRepo.save(favorite);
                return favorite;
            }
        } else
            throw new CollectionEntryAlreadyExistException("Pokemon with name " + favoriteDTO.name() + " already added to favorites");
        return null;
    }

    public Favorite updateFavorite(String id, UpdateFavoriteDTO updateFavoriteDTO) {
        Favorite oldFavorite = favoriteRepo.findById(id).orElse(null);
        if (oldFavorite == null) {
            throw new CollectionEntryNotFoundException("Favorite Pokemon with id: " + id + " not found");
        }
        return favoriteRepo.save(oldFavorite.withNickname(updateFavoriteDTO.nickname()));
    }

    public void deleteFavoriteById(String id) {
        Favorite favorite = favoriteRepo.findById(id).orElse(null);
        if (Objects.isNull(favorite)) {
            throw new CollectionEntryNotFoundException("Favorite Pokemon with id: " + id + " not found");
        }
        favoriteRepo.delete(favorite);
    }

    public List<Favorite> searchFavorites(String name, String nickname) {
        boolean hasName = StringUtils.hasText(name);
        boolean hasNickname = StringUtils.hasText(nickname);

        if (!hasName && !hasNickname) {
            throw new IllegalArgumentException("Must provide either a name or a nickname");
        }
        if (hasName && hasNickname) {
            return favoriteRepo.searchByNameContainsAndNicknameContainsIgnoreCase(name.trim(), nickname.trim());
        }
        if (hasName) {
            return favoriteRepo.searchByNameContainsIgnoreCase(name.trim());
        }

        return favoriteRepo.searchByNicknameContainsIgnoreCase(nickname.trim());
    }
}
