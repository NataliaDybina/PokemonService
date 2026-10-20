package org.example.pokemonservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FavoriteDTO(
        @NotBlank @Size(min = 2, message = "Name should contain at least 2 symbols")
        String name,
        @NotBlank @Size(min = 2, message = "Nickname should contain at least 2 symbols")
        String nickname) {
}
