package org.example.pokemonservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFavoriteDTO(
        @NotBlank @Size(min = 2, message = "Nickname should contain at least 2 symbols")
        String nickname) {
}
