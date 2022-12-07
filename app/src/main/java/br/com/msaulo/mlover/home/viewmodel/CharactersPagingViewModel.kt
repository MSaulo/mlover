package br.com.msaulo.mlover.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import br.com.msaulo.mlover.api.marvel.character.CharactersListResponse
import br.com.msaulo.mlover.home.paging.CharactersPaging
import br.com.msaulo.mlover.service.MarvelService
import br.com.msaulo.mlover.service.RepositoryService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class CharactersPagingViewModel(app: Application): AndroidViewModel(app) {
    val api = MarvelService()
    val repository = RepositoryService(app.applicationContext)

    val characterList = Pager(PagingConfig(1)) {
        CharactersPaging(api, repository)
    }.flow.cachedIn(viewModelScope)

    fun getFavorites(): ArrayList<CharactersListResponse.Data.Result> {
        val list = ArrayList<CharactersListResponse.Data.Result>()
        val favorites = repository.favoriteCharacter.getAll()

        favorites.forEach {
            it.data?.let { data ->
                list.add(Json.decodeFromString(data))
            }
        }

        return list
    }
}