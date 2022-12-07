package br.com.msaulo.mlover.home

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.msaulo.mlover.R
import br.com.msaulo.mlover.characterdetails.CharacterDetailsActivity
import br.com.msaulo.mlover.databinding.FragmentHomeBinding
import br.com.msaulo.mlover.home.adapter.CharactersAdapter
import br.com.msaulo.mlover.home.adapter.CharactersPagingAdapter
import br.com.msaulo.mlover.home.viewmodel.CharactersPagingViewModel
import br.com.msaulo.mlover.charactersearch.SearchActivity
import br.com.msaulo.mlover.repository.FavoriteCharacter
import br.com.msaulo.mlover.service.RepositoryService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: CharactersPagingViewModel
    private lateinit var repository: RepositoryService
    private lateinit var charactersPagingAdapter: CharactersPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        charactersPagingAdapter = CharactersPagingAdapter()
        repository = RepositoryService(requireContext())
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[CharactersPagingViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            recyclerViewCharactersFavorites.adapter = charactersAdapter(favoriteEmpty)
            mainLayout.requestFocus()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val searchManager = inflater.context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchCharacters.apply {
            setSearchableInfo(searchManager.getSearchableInfo(
                ComponentName(inflater.context, SearchActivity::class.java))
            )
            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (!hasFocus)  {
                    view.hideKeyboard()
                }
            }
        }

        binding.apply {
            lifecycleScope.launchWhenCreated {
                viewModel.characterList.collect { it ->
                    charactersPagingAdapter.addLoadStateListener { loadState ->
                        if (loadState.refresh is LoadState.Error) {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.internet_fail),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    charactersPagingAdapter.submitData(it)
                }
            }

            charactersPagingAdapter.setOnItemClickListener { launchCharacterDetails(it.id) }

            charactersPagingAdapter.setOnFavoriteClickListener {
                lifecycleScope.launchWhenCreated {
                    if (!it.favorite) {
                        repository.favoriteCharacter.deleteByCode(it.id)
                    } else {
                        val favorite = FavoriteCharacter(null, it.id, Json.encodeToString(it))
                        repository.favoriteCharacter.insert(favorite)
                    }
                    recyclerViewCharactersFavorites.adapter = charactersAdapter(favoriteEmpty)
                }
            }

            lifecycleScope.launchWhenCreated {
                charactersPagingAdapter.loadStateFlow.collect{
                    val state = it.refresh
                    progressBarCharacters.isVisible = state is LoadState.Loading
                }
            }

            recyclerViewCharacters.apply {
                layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = charactersPagingAdapter
            }


            recyclerViewCharactersFavorites.apply {
                layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = charactersAdapter(favoriteEmpty)
            }
        }

        return binding.root
    }

    private fun View.hideKeyboard() {
        clearFocus()
        val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(windowToken, 0)
        // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun launchCharacterDetails(characterId: Int) {
        val intent = Intent(context, CharacterDetailsActivity::class.java)
        intent.putExtra("characterId", characterId)
        startActivity(intent)
    }

    private fun charactersAdapter(emptyTextView: TextView): CharactersAdapter {
        val charactersAdapter = CharactersAdapter(viewModel.getFavorites())
        emptyTextView.isVisible = charactersAdapter.itemCount == 0
        charactersAdapter.setOnItemClickListener {
            launchCharacterDetails(it.id)
        }
        return charactersAdapter
    }
}