package com.cartoon.pokemon.model;


import lombok.Data;
import java.util.List;

@Data
public class PokemonListResponse {
    private int count;
    private String next;
    private String previous;
    private List<Pokemon> results;
}

