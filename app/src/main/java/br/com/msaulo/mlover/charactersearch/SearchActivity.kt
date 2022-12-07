package br.com.msaulo.mlover.charactersearch

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.msaulo.mlover.R
import br.com.msaulo.mlover.api.marvel.character.CharactersListResponse
import br.com.msaulo.mlover.characterdetails.CharacterDetailsActivity
import br.com.msaulo.mlover.charactersearch.adapter.CharactersSearchPagingAdapter
import br.com.msaulo.mlover.charactersearch.paging.CharactersSearchPaging
import br.com.msaulo.mlover.databinding.ActivitySearchBinding
import br.com.msaulo.mlover.service.MarvelService
import kotlinx.coroutines.flow.Flow

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var api: MarvelService
    private lateinit var charactersSearchPagingAdapter: CharactersSearchPagingAdapter
    private lateinit var characterSearchList: Flow<PagingData<CharactersListResponse.Data.Result>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = MarvelService()
        charactersSearchPagingAdapter = CharactersSearchPagingAdapter()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                binding.searchTerm.text = query

                characterSearchList = Pager(PagingConfig(1)) {
                    CharactersSearchPaging(api, query)
                }.flow.cachedIn(lifecycleScope)
            }
        }

        binding.apply {
            backHeader.setOnClickListener {
                onBackPressed()
            }

            searchTerm.setOnClickListener {
                onBackPressed()
            }

            lifecycleScope.launchWhenCreated {
                characterSearchList.collect {
                    charactersSearchPagingAdapter.addLoadStateListener { loadState ->
                        if (loadState.refresh is LoadState.Error) {
                            Toast.makeText(
                                baseContext,
                                resources.getString(R.string.internet_fail),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            charactersSearchPagingAdapter.itemCount < 1) {
                            searchEmpty.isVisible = true
                        }
                    }
                    charactersSearchPagingAdapter.submitData(it)
                }
            }

            lifecycleScope.launchWhenCreated {
                charactersSearchPagingAdapter.loadStateFlow.collect{
                    val state = it.refresh
                    progressBarSearchCharacters.isVisible = state is LoadState.Loading
                }
            }

            charactersSearchPagingAdapter.setOnItemClickListener {
                val intent = Intent(baseContext, CharacterDetailsActivity::class.java)
                intent.putExtra("characterId", it.id)
                startActivity(intent)
            }

            recyclerViewCharactersSearch.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = charactersSearchPagingAdapter
            }
        }

    }















}