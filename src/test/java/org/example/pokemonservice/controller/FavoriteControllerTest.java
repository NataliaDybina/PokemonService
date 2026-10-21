package org.example.pokemonservice.controller;

import org.example.pokemonservice.model.Favorite;
import org.example.pokemonservice.repository.FavoriteRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private FavoriteRepo favoriteRepo;

    @BeforeEach
    void setUp() {
        favoriteRepo.deleteAll();
    }

    @Test
    void getAllFavorites() throws Exception {
        Favorite favorite = new Favorite("1", "Pikachu", "Lovely", 1, 1, 1, 1, null);
        favoriteRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Pikachu"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nickname").value("Lovely"));
    }

    @Test
    void getFavoriteById() throws Exception {
        Favorite favorite = new Favorite("1", "Pikachu", "Lovely", 1, 1, 1, 1, null);
        favoriteRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Pikachu"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("Lovely"));
    }

    @Test
    void addFavorite() throws Exception {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/pikachu"))
                .andRespond(withSuccess("""
                        {
                           "id": 25,
                                                 "name": "pikachu",
                                                 "height": 4,
                                                 "weight": 60,
                           "types": [
                                   {
                                        "slot": 1,
                                        "type": {
                                          "name": "electric",
                                          "url": "https://pokeapi.co/api/v2/type/13/"
                                        }
                                    }
                             ]
                        }
                        """, MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                                                        "name": "pikachu",
                                                                        "nickname": "Lovely"
                                                                        }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                         {
                               "name": "pikachu",
                               "nickname": "Lovely",
                               "pokemonId": 25,
                               "height": 4,
                               "weight": 60,
                               "types": [
                                           {
                                               "name": "electric",
                                               "url": "https://pokeapi.co/api/v2/type/13/"
                                           }
                                       ]                                 }
                        """))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());
    }

    @Test
    void updateFavorite() throws Exception {
        Favorite favorite = new Favorite("1", "Pikachu", "Lovely", 1, 1, 1, 1, null);
        favoriteRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/collection/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nickname" : "Cool"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Pikachu"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("Cool"));
    }

    @Test
    void deleteFavorite() throws Exception {
        Favorite favorite = new Favorite("1", "Pikachu", "Lovely", 1, 1, 1, 1, null);
        favoriteRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/collection/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void searchFavorites() throws Exception {
        Favorite favorite = new Favorite("1", "Pikachu", "Lovely", 1, 1, 1, 1, null);
        favoriteRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection/search?name=Pika"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Pikachu"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection/search?nickname=lo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Pikachu"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection/search?name=Pi&nickname=lo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Pikachu"));

    }
}