package org.example.pokemonservice.model;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Document(collection = "favorites")
@With
public record Favorite(@Id
                       String id,
                       String name,
                       String nickname,
                       @Indexed(unique = true) Integer pokemonId,
                       Integer base_experience,
                       Integer height,
                       Integer weight) {

    public Favorite {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
