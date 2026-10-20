package org.example.pokemonservice.repository;

import org.example.pokemonservice.model.Favorite;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepo extends MongoRepository<Favorite, String> {
    boolean existsByName(String name);

    boolean existsById(@NonNull String id);

    Favorite getFavoriteByName(@NonNull String name);

    List<Favorite> searchByNicknameContainsIgnoreCase(String nickname);

    List<Favorite> searchByNameContainsIgnoreCase(String name);

    List<Favorite> searchByNameContainsAndNicknameContainsIgnoreCase(String name, String nickname);
}
