package br.com.msaulo.mlover.charactersearch.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import br.com.msaulo.mlover.api.marvel.character.CharactersListResponse
import br.com.msaulo.mlover.service.MarvelService
import retrofit2.HttpException


class CharactersSearchPaging(
    private val api: MarvelService,
    private val q: String,
): PagingSource<Int, CharactersListResponse.Data.Result>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharactersListResponse.Data.Result> {
        return try {
            val currentPage = params.key ?: 0
            val response = api.character.getCharactersListByName(q, currentPage)
            val data = response.body()!!.data.results
            val totalElements = response.body()!!.data.count
            val responseData = mutableListOf<CharactersListResponse.Data.Result>()
            responseData.addAll(data)

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