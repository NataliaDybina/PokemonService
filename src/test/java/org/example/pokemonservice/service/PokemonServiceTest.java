package org.example.pokemonservice.service;

import org.example.pokemonservice.api.AllPokemonEntity;
import org.example.pokemonservice.api.PokemonResponse;
import org.example.pokemonservice.dto.ComparisonDTO;
import org.example.pokemonservice.exception.PokemonNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RestClientTest(PokemonService.class)
class PokemonServiceTest {

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void getAllPokemon_Success() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "count": 1,
                          "results": [
                            {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"}
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        List<AllPokemonEntity> results = pokemonService.getAllPokemon();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("bulbasaur", results.getFirst().name());
        mockServer.verify();
    }

    @Test
    void getPokemonByName_Success() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/pikachu"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "id": 25,
                          "name": "pikachu",
                          "base_experience": 112,
                          "height": 4,
                          "weight": 60,
                          "types": []
                        }
                        """, MediaType.APPLICATION_JSON));

        PokemonResponse response = pokemonService.getPokemonByName("pikachu");

        assertNotNull(response);
        assertEquals("pikachu", response.name());
        assertEquals(25, response.id());
        mockServer.verify();
    }

    @Test
    void getPokemonByName_NotFound_ThrowsException() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/unknown"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        PokemonNotFoundException exception = assertThrows(
                PokemonNotFoundException.class,
                () -> pokemonService.getPokemonByName("unknown")
        );

        assertEquals("Pokemon with name unknown not Found", exception.getMessage());
        mockServer.verify();
    }

    @Test
    void getRandomPokemon_Success() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "count": 1,
                          "results": [
                            {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"}
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/bulbasaur"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "id": 1,
                          "name": "bulbasaur",
                          "height": 7,
                          "weight": 69,
                          "types": []
                        }
                        """, MediaType.APPLICATION_JSON));

        PokemonResponse response = pokemonService.getRandomPokemon();

        assertNotNull(response);
        assertEquals("bulbasaur", response.name());
        assertEquals(1, response.id());
        mockServer.verify();
    }

    @Test
    void compare2Pokemon_Success() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/pikachu"))
                .andRespond(withSuccess("""
                        {"id": 25, "name": "pikachu", "weight": 60, "height": 4, "types": []}
                        """, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/snorlax"))
                .andRespond(withSuccess("""
                        {"id": 143, "name": "snorlax", "weight": 4600, "height": 21, "types": []}
                        """, MediaType.APPLICATION_JSON));

        ComparisonDTO result = pokemonService.compare2Pokemon("pikachu", "snorlax");

        assertNotNull(result);
        assertEquals("snorlax", result.heavierPokemon());
        assertEquals(4540, result.weightDifference());
        assertEquals("snorlax", result.higherPokemon());
        assertEquals(17, result.heightDifference());

        mockServer.verify();
    }

    @Test
    void compare2Pokemon_EqualStats() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/pika1"))
                .andRespond(withSuccess("""
                        {"id": 1, "name": "pika1", "weight": 50, "height": 10, "types": []}
                        """, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/pika2"))
                .andRespond(withSuccess("""
                        {"id": 2, "name": "pika2", "weight": 50, "height": 10, "types": []}
                        """, MediaType.APPLICATION_JSON));

        ComparisonDTO result = pokemonService.compare2Pokemon("pika1", "pika2");

        assertEquals("They're equal", result.heavierPokemon());
        assertEquals(0, result.weightDifference());
        assertEquals("They're equal", result.higherPokemon());
        assertEquals(0, result.heightDifference());

        mockServer.verify();
    }

    @Test
    void getPokemonByType_Success() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/type/fire"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "pokemon": [
                            {
                              "pokemon": {
                                "name": "charmander",
                                "url": "https://pokeapi.co/api/v2/pokemon/4/"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        List<AllPokemonEntity> results = pokemonService.getPokemonByType("fire");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("charmander", results.getFirst().name());
        mockServer.verify();
    }

    @Test
    void getPokemonByType_NotFound_ThrowsException() {
        mockServer.expect(requestTo("https://pokeapi.co/api/v2/type/invalid"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pokemonService.getPokemonByType("invalid")
        );

        assertEquals("Type invalid not Found", exception.getMessage());
        mockServer.verify();
    }
}