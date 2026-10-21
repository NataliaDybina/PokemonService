package org.example.pokemonservice.controller;

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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void getAllPokemon() throws Exception {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon"))
                .andRespond(withSuccess("""
                        {
                                                                         "count": 1351,
                                                                         "results": [
                                                                           {
                                                                             "name": "bulbasaur",
                                                                             "url": "https://pokeapi.co/api/v2/pokemon/1/"
                                                                           }
                                                                         ]
                        }
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("bulbasaur"));
    }

    @Test
    void getPokemonByName() throws Exception {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/bulbasaur"))
                .andRespond(withSuccess("""
                        {
                           "id": 1,
                           "name": "bulbasaur",
                           "types": [
                               {
                                 "slot": 1,
                                 "type": {
                                   "name": "grass",
                                   "url": "https://pokeapi.co/api/v2/type/12/"
                                 }
                               },
                               {
                                 "slot": 2,
                                 "type": {
                                   "name": "poison",
                                   "url": "https://pokeapi.co/api/v2/type/4/"
                                 }
                               }
                             ]
                        }
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/name/bulbasaur"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("bulbasaur"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.types[0].type.name").value("grass"));
    }

    @Test
    void gerRandomPokemon() throws Exception {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0"))
                .andRespond(withSuccess("""
                        {
                                                                         "count": 1,
                                                                         "results": [
                                                                           {
                                                                             "name": "bulbasaur",
                                                                             "url": "https://pokeapi.co/api/v2/pokemon/1/"
                                                                           }
                                                                         ]
                        }
                        """, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/bulbasaur"))
                .andRespond(withSuccess("""
                        {
                           "id": 1,
                           "name": "bulbasaur",
                           "base_experience": 64,
                           "height": 7,
                           "is_default": true,
                           "order": 1,
                           "weight": 69,
                           "types": [
                               {
                                 "slot": 1,
                                 "type": {
                                   "name": "grass",
                                   "url": "https://pokeapi.co/api/v2/type/12/"
                                 }
                               },
                               {
                                 "slot": 2,
                                 "type": {
                                   "name": "poison",
                                   "url": "https://pokeapi.co/api/v2/type/4/"
                                 }
                               }
                             ]
                        }
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/random"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("bulbasaur"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.types[0].type.name").value("grass"));
    }

    @Test
    void compare2Pokemon() throws Exception {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/bulbasaur"))
                .andRespond(withSuccess("""
                        {
                           "id": 1,
                           "name": "bulbasaur",
                           "height": 7,
                           "weight": 69
                        }
                        """, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/pikachu"))
                .andRespond(withSuccess("""
                        {
                           "id": 25,
                           "name": "pikachu",
                           "height": 4,
                           "weight": 60
                        }
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/compare/bulbasaur/pikachu"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "heavierPokemon": "bulbasaur",
                             "weightDifference": 9,
                             "higherPokemon": "bulbasaur",
                             "heightDifference": 3
                             }
                        """));
    }

    @Test
    void getPokemonByType() throws Exception {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/type/fire"))
                .andRespond(withSuccess("""
                        {
                           "id": 10,
                           "name": "fire",
                           "pokemon": [
                               {
                                 "slot": 1,
                                 "pokemon": {
                                   "name": "charmander",
                                   "url": "https://pokeapi.co/api/v2/pokemon/4/"
                                 }
                               }
                           ]
                        }
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/type/fire"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        [
                                  {
                                      "name": "charmander",
                                      "url": "https://pokeapi.co/api/v2/pokemon/4/"
                                  }
                        ]
                        """));
    }
}