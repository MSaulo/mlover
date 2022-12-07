package br.com.msaulo.mlover.characterdetails

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.msaulo.mlover.R
import br.com.msaulo.mlover.databinding.ActivityCharacterDetailsBinding
import br.com.msaulo.mlover.repository.FavoriteCharacter
import br.com.msaulo.mlover.service.MarvelService
import br.com.msaulo.mlover.service.RepositoryService
import coil.load
import coil.size.Scale
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class CharacterDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterDetailsBinding
    private lateinit var api: MarvelService
    private lateinit var repository: RepositoryService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = MarvelService()
        repository = RepositoryService(baseContext)

        binding = ActivityCharacterDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarColor = resources.getColor(R.color.orange_700, theme)

        val characterId = intent.getIntExtra("characterId", 0)

        binding.apply {
            backHeader.setOnClickListener {
                onBackPressed()
            }

            lifecycleScope.launch {
                val res = api.character.getCharacter(characterId)
                if (res.isSuccessful) {
                    val characterDetails = res.body()!!.data.results[0]
                    val thumbnail = "${characterDetails.thumbnail.path}.${characterDetails.thumbnail.extension}"
                        .replace("http://", "https://")
                    image.load(thumbnail) {
                        crossfade(true)
                        scale(Scale.FILL)
                    }
                    name.text = characterDetails.name
                    description.text  = if (characterDetails.description.isNotBlank()) characterDetails.description else "Descrição não disponível"

                    val favorites = repository.favoriteCharacter.getAll()
                    characterDetails.favorite = favorites.any { characterDetails.id == it.code }

                    if (characterDetails.favorite) {
                        favorite.setImageResource(R.drawable.ic_heart_fill)
                    }

                    favorite.setOnClickListener {
                        lifecycleScope.launchWhenCreated {
                            if (characterDetails.favorite) {
                                repository.favoriteCharacter.deleteByCode(characterDetails.id)
                                characterDetails.favorite = false
                                favorite.setImageResource(R.drawable.ic_heart)
                            } else {
                                val favoriteCharacter = FavoriteCharacter(null, characterDetails.id, Json.encodeToString(characterDetails))
                                repository.favoriteCharacter.insert(favoriteCharacter)
                                characterDetails.favorite = true
                                favorite.setImageResource(R.drawable.ic_heart_fill)
                            }
                        }
                    }
                } else {
                    Toast.makeText(baseContext, resources.getText(R.string.internet_fail), Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}