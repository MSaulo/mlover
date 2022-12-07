package br.com.msaulo.mlover.api.marvel

import br.com.msaulo.mlover.api.marvel.character.CharacterResponse
import br.com.msaulo.mlover.api.marvel.character.CharactersListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelCharacterInterface {
    @GET("/v1/public/characters")
    suspend fun getCharactersList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = 20
    ): Response<CharactersListResponse>

    @GET("/v1/public/characters")
    suspend fun getCharactersListByName(
        @Query("nameStartsWith") q: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = 20
    ): Response<CharactersListResponse>

    @GET("/v1/public/characters/{characterId}")
    suspend fun getCharacter(
        @Path(value = "characterId", encoded = true) id: Int
    ): Response<CharacterResponse>
}