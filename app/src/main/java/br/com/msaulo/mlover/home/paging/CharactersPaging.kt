package br.com.msaulo.mlover.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import br.com.msaulo.mlover.api.marvel.character.CharactersListResponse
import br.com.msaulo.mlover.service.MarvelService
import br.com.msaulo.mlover.service.RepositoryService
import retrofit2.HttpException


class CharactersPaging(
    private val api: MarvelService,
    private val repository: RepositoryService
): PagingSource<Int, CharactersListResponse.Data.Result>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharactersListResponse.Data.Result> {
        return try {
            val currentPage = params.key ?: 0
            val response = api.character.getCharactersList(currentPage)
            val data = response.body()!!.data.results
            val totalElements = response.body()!!.data.count
            val favorites = repository.favoriteCharacter.getAll()
            val responseData = mutableListOf<CharactersListResponse.Data.Result>()

            responseData.addAll(data)
            responseData.forEach { r ->
                r.favorite = favorites.any { r.id == it.code }
            }

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 20) null else -1,
                nextKey = if (currentPage < totalElements) currentPage.plus(20) else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharactersListResponse.Data.Result>): Int? {
        return null
    }
}