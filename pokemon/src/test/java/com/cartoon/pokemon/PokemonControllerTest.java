package com.cartoon.pokemon;

import com.cartoon.pokemon.controller.PokemonController;
import com.cartoon.pokemon.model.Pokemon;
import com.cartoon.pokemon.model.PokemonListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PokemonControllerTest {

	private MockMvc mockMvc;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private CacheManager cacheManager;

	@InjectMocks
	private PokemonController pokemonController;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(pokemonController).build();
	}

	@Test
	public void testGetPokemonList_Success() throws Exception {
		// Mock response data
		Pokemon mockPokemon1 = new Pokemon();
		mockPokemon1.setName("bulbasaur");
		mockPokemon1.setUrl("https://pokeapi.co/api/v2/pokemon/1/");

		Pokemon mockPokemon2 = new Pokemon();
		mockPokemon2.setName("ivysaur");
		mockPokemon2.setUrl("https://pokeapi.co/api/v2/pokemon/2/");

		PokemonListResponse mockResponse = new PokemonListResponse();
		mockResponse.setCount(1302);
		mockResponse.setResults(List.of(mockPokemon1, mockPokemon2));

		// Mocking RestTemplate response
		when(restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon/?offset=0&limit=20", PokemonListResponse.class))
				.thenReturn(mockResponse);

		mockMvc.perform(get("/api/pokemon/list")
						.param("offset", "0")
						.param("limit", "20"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1302))
				.andExpect(jsonPath("$.results[0].name").value("bulbasaur"))
				.andExpect(jsonPath("$.results[1].name").value("ivysaur"));

		// Verify that RestTemplate was called once
		verify(restTemplate, times(1)).getForObject(anyString(), eq(PokemonListResponse.class));
	}

	@Test
	public void testGetPokemonList_EmptyResponse() throws Exception {
		// Mocking an empty response
		PokemonListResponse mockEmptyResponse = new PokemonListResponse();
		mockEmptyResponse.setCount(0);
		mockEmptyResponse.setResults(Collections.emptyList());

		// Mocking RestTemplate response
		when(restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon/?offset=0&limit=20", PokemonListResponse.class))
				.thenReturn(mockEmptyResponse);

		mockMvc.perform(get("/api/pokemon/list")
						.param("offset", "0")
						.param("limit", "20"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(0))
				.andExpect(jsonPath("$.results").isEmpty());

		// Verify that RestTemplate was called once
		verify(restTemplate, times(1)).getForObject(anyString(), eq(PokemonListResponse.class));
	}

	@Test
	public void testGetPokemonList_LargeOffset() throws Exception {
		// Mocking a large offset response
		PokemonListResponse mockLargeOffsetResponse = new PokemonListResponse();
		mockLargeOffsetResponse.setCount(1302);
		mockLargeOffsetResponse.setResults(Collections.emptyList());  // No Pokémon available at that offset

		// Mocking RestTemplate response
		when(restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon/?offset=1000&limit=20", PokemonListResponse.class))
				.thenReturn(mockLargeOffsetResponse);

		mockMvc.perform(get("/api/pokemon/list")
						.param("offset", "1000")
						.param("limit", "20"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1302))
				.andExpect(jsonPath("$.results").isEmpty());

		// Verify that RestTemplate was called once
		verify(restTemplate, times(1)).getForObject(anyString(), eq(PokemonListResponse.class));
	}

	@Test
	public void testGetPokemonList_ApiError() throws Exception {
		// Simulate API failure (e.g., timeouts or other errors)
		when(restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon/?offset=0&limit=20", PokemonListResponse.class))
				.thenThrow(new RuntimeException("API call failed"));

		mockMvc.perform(get("/api/pokemon/list")
						.param("offset", "0")
						.param("limit", "20"))
				.andExpect(status().isInternalServerError())  // Expecting a server error due to failure
				.andExpect(jsonPath("$.error").value("API call failed"));

		// Verify that RestTemplate was called once
		verify(restTemplate, times(1)).getForObject(anyString(), eq(PokemonListResponse.class));
	}
}
