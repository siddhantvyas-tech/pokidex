// src/components/PokemonSearch.js
import React, { useState } from "react";
import { useQuery } from "react-query";
import axios from "axios";

// Function to fetch Pokémon by name
const fetchPokemonByName = async (name) => {
  console.log(name);
  const { data } = await axios.get(
    `http://localhost:8080/api/pokemon/search?name=${name.toLowerCase()}`
  );
  return data;
};

const PokemonSearch = () => {
  const [search, setSearch] = useState("");  // State to hold user input
  const [pokemonName, setPokemonName] = useState("");  // State to trigger search

  // useQuery to fetch the Pokémon data when pokemonName is updated
  const { data, error, isLoading, isError } = useQuery(
    ["pokemon", pokemonName],  // Query key with pokemonName
    () => fetchPokemonByName(pokemonName),  // Fetch function
    {
      enabled: !!pokemonName,  // Only run the query when pokemonName is not empty
      cacheTime: 60000,  // Cache the result for 60 seconds
      retry: 2,  // Retry the request twice on failure
    }
  );

  // Handle form submission and trigger search
  const handleSubmit = (e) => {
    e.preventDefault();
    setPokemonName(search);
  };

  return (
    <div className="pokemon-search-container">
      <h1>Pokémon Search</h1>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Enter Pokémon Name"
          required
        />
        <button type="submit">Search</button>
      </form>

      {isLoading && <div>Loading...</div>}
      {isError && <div>Error: {error.message}</div>}
      {data && (
        <div className="pokemon-info">
          <h2>{data.name.charAt(0).toUpperCase() + data.name.slice(1)}</h2>
          <img
            src={data.sprites.front_default}
            alt={data.name}
            style={{ width: "200px" }}
          />
          <p>Height: {data.height / 10} m</p>
          <p>Weight: {data.weight / 10} kg</p>
          <p>Types:</p>
          <ul>
            {data.types.map((type) => (
              <li key={type.type.name}>{type.type.name}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default PokemonSearch;
