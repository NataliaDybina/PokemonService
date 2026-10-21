package org.example.pokemonservice.service;

import org.example.pokemonservice.api.PokemonResponse;
import org.example.pokemonservice.api.Types;
import org.example.pokemonservice.dto.FavoriteDTO;
import org.example.pokemonservice.dto.UpdateFavoriteDTO;
import org.example.pokemonservice.exception.CollectionEntryAlreadyExistException;
import org.example.pokemonservice.exception.CollectionEntryNotFoundException;
import org.example.pokemonservice.model.Favorite;
import org.example.pokemonservice.model.Type;
import org.example.pokemonservice.repository.FavoriteRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class FavoriteServiceTest {

    private final FavoriteRepo repo = Mockito.mock(FavoriteRepo.class);
    private final PokemonService pokemonService = Mockito.mock(PokemonService.class);
    private final FavoriteService service = new FavoriteService(repo, pokemonService);

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repo, pokemonService);
    }

    @Test
    void getFavoriteById_WhenExists_ReturnsFavorite() {
        String expectedId = "25";
        Favorite expectedFavorite = new Favorite(expectedId, "Pikachu", "Cool", 1, 1, 1, 1, null);

        when(repo.findById(expectedId))
                .thenReturn(Optional.of(expectedFavorite));

        Favorite actualFavorite = service.getFavoriteById(expectedId);

        Assertions.assertNotNull(actualFavorite);
        Assertions.assertEquals(expectedId, actualFavorite.id());
        Assertions.assertEquals("Pikachu", actualFavorite.name());

        verify(repo, times(1)).findById(expectedId);
    }

    @Test
    void getFavoriteById_WhenNotFound_ThrowsException() {
        String nonExistentId = "999";

        Mockito.when(repo.findById(nonExistentId))
                .thenReturn(Optional.empty());

        CollectionEntryNotFoundException exception = Assertions.assertThrows(
                CollectionEntryNotFoundException.class,
                () -> service.getFavoriteById(nonExistentId)
        );

        Assertions.assertEquals("Favorite Pokemon with id: 999 not found", exception.getMessage());
        Mockito.verify(repo, Mockito.times(1)).findById(nonExistentId);
    }

    @Test
    void addFavorite_WhenPokemonDoesNotExistInRepoAndFoundInApi_SavesAndReturnsFavorite() {
        FavoriteDTO dto = new FavoriteDTO("Pikachu", "Cool");
        Types typeObj = new Types(1, new Type("electric", "url"));
        PokemonResponse pokemonResponse = new PokemonResponse(25, "Pikachu", 112, 4, 60, List.of(typeObj));

        when(repo.existsByName("Pikachu")).thenReturn(false);
        when(pokemonService.getPokemonByName("Pikachu")).thenReturn(pokemonResponse);

        Favorite actual = service.addFavorite(dto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Pikachu", actual.name());
        Assertions.assertEquals("Cool", actual.nickname());
        Assertions.assertEquals(25, actual.pokemonId());
        Assertions.assertNotNull(actual.id());

        verify(repo, times(1)).existsByName("Pikachu");
        verify(pokemonService, times(1)).getPokemonByName("Pikachu");
        verify(repo, times(1)).save(any(Favorite.class));
    }

    @Test
    void addFavorite_WhenPokemonAlreadyExistsInRepo_ThrowsException() {
        FavoriteDTO dto = new FavoriteDTO("Pikachu", "Cool");

        when(repo.existsByName("Pikachu")).thenReturn(true);

        CollectionEntryAlreadyExistException exception = Assertions.assertThrows(
                CollectionEntryAlreadyExistException.class,
                () -> service.addFavorite(dto)
        );

        Assertions.assertEquals("Pokemon with name Pikachu already added to favorites", exception.getMessage());
        verify(repo, times(1)).existsByName("Pikachu");
    }

    @Test
    void addFavorite_WhenPokemonNotFoundInApi_ReturnsNull() {
        FavoriteDTO dto = new FavoriteDTO("Unknown", "Mystery");

        when(repo.existsByName("Unknown")).thenReturn(false);
        when(pokemonService.getPokemonByName("Unknown")).thenReturn(null);

        Favorite actual = service.addFavorite(dto);

        Assertions.assertNull(actual);
        verify(repo, times(1)).existsByName("Unknown");
        verify(pokemonService, times(1)).getPokemonByName("Unknown");
    }

    @Test
    void updateFavorite_WhenExists_UpdatesAndReturnsFavorite() {
        String id = "25";
        UpdateFavoriteDTO updateDTO = new UpdateFavoriteDTO("SuperCool");
        Favorite oldFavorite = new Favorite(id, "Pikachu", "Cool", 25, 112, 4, 60, List.of());
        Favorite updatedFavorite = new Favorite(id, "Pikachu", "SuperCool", 25, 112, 4, 60, List.of());

        when(repo.findById(id)).thenReturn(Optional.of(oldFavorite));
        when(repo.save(any(Favorite.class))).thenReturn(updatedFavorite);

        Favorite actual = service.updateFavorite(id, updateDTO);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals("SuperCool", actual.nickname());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(any(Favorite.class));
    }

    @Test
    void updateFavorite_WhenNotFound_ThrowsException() {
        String nonExistentId = "999";
        UpdateFavoriteDTO updateDTO = new UpdateFavoriteDTO("SuperCool");

        when(repo.findById(nonExistentId)).thenReturn(Optional.empty());

        CollectionEntryNotFoundException exception = Assertions.assertThrows(
                CollectionEntryNotFoundException.class,
                () -> service.updateFavorite(nonExistentId, updateDTO)
        );

        Assertions.assertEquals("Favorite Pokemon with id: 999 not found", exception.getMessage());
        verify(repo, times(1)).findById(nonExistentId);
    }

    @Test
    void deleteFavoriteById_WhenExists_DeletesFavorite() {
        String id = "25";
        Favorite favorite = new Favorite(id, "Pikachu", "Cool", 25, 112, 4, 60, List.of());

        when(repo.findById(id)).thenReturn(Optional.of(favorite));

        service.deleteFavoriteById(id);

        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).delete(favorite);
    }

    @Test
    void deleteFavoriteById_WhenNotFound_ThrowsException() {
        String nonExistentId = "999";

        when(repo.findById(nonExistentId)).thenReturn(Optional.empty());

        CollectionEntryNotFoundException exception = Assertions.assertThrows(
                CollectionEntryNotFoundException.class,
                () -> service.deleteFavoriteById(nonExistentId)
        );

        Assertions.assertEquals("Favorite Pokemon with id: 999 not found", exception.getMessage());
        verify(repo, times(1)).findById(nonExistentId);
    }

    @Test
    void searchFavorites_WhenBothNameAndNicknameProvided_CallsSearchByNameAndNickname() {
        Favorite favorite = new Favorite("25", "Pikachu", "Cool", 25, 112, 4, 60, List.of());
        when(repo.searchByNameContainsAndNicknameContainsIgnoreCase("Pika", "Cool"))
                .thenReturn(List.of(favorite));

        List<Favorite> results = service.searchFavorites("  Pika  ", "  Cool  ");

        Assertions.assertEquals(1, results.size());
        verify(repo, times(1)).searchByNameContainsAndNicknameContainsIgnoreCase("Pika", "Cool");
    }

    @Test
    void searchFavorites_WhenOnlyNameProvided_CallsSearchByName() {
        Favorite favorite = new Favorite("25", "Pikachu", "Cool", 25, 112, 4, 60, List.of());
        when(repo.searchByNameContainsIgnoreCase("Pika"))
                .thenReturn(List.of(favorite));

        List<Favorite> results = service.searchFavorites("  Pika  ", null);

        Assertions.assertEquals(1, results.size());
        verify(repo, times(1)).searchByNameContainsIgnoreCase("Pika");
    }

    @Test
    void searchFavorites_WhenOnlyNicknameProvided_CallsSearchByNickname() {
        Favorite favorite = new Favorite("25", "Pikachu", "Cool", 25, 112, 4, 60, List.of());
        when(repo.searchByNicknameContainsIgnoreCase("Cool"))
                .thenReturn(List.of(favorite));

        List<Favorite> results = service.searchFavorites("", "  Cool  ");

        Assertions.assertEquals(1, results.size());
        verify(repo, times(1)).searchByNicknameContainsIgnoreCase("Cool");
    }

    @Test
    void searchFavorites_WhenNeitherNameNorNicknameProvided_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.searchFavorites("  ", null)
        );

        Assertions.assertEquals("Must provide either a name or a nickname", exception.getMessage());
    }
}