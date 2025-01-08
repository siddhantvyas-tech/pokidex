package com.cartoon.pokemon.controller;

import com.cartoon.pokemon.model.Pokemon;
import com.cartoon.pokemon.model.PokemonListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")  // Allow CORS for React frontend
//@EnableCaching
@RestController
@RequiredArgsConstructor
public class PokemonController {

    @Autowired
    private final RestTemplate restTemplate;

//    @Cacheable(value = "pokemon", key = "#name")
    @GetMapping("/api/pokemon/search")
    public Pokemon searchPokemonByName(@RequestParam String name) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + name.toLowerCase();
        return restTemplate.getForObject(url, Pokemon.class);
    }

//    @Cacheable(value = "pokemon", key = "#id")
    @GetMapping("/api/pokemon/{id}")
    public Pokemon getPokemonById(@RequestParam Long id) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + id;
        return restTemplate.getForObject(url, Pokemon.class);
    }

    // New method to list multiple Pokémon
//    @Cacheable(value = "pokemonList", key = "#offset")
    @GetMapping("/api/pokemon/list")
    public PokemonListResponse getPokemonList(@RequestParam(defaultValue = "0") int offset,
                                              @RequestParam(defaultValue = "20") int limit) {
        String url = String.format("https://pokeapi.co/api/v2/pokemon/?offset=%d&limit=%d", offset, limit);
        return restTemplate.getForObject(url, PokemonListResponse.class);
    }
}
