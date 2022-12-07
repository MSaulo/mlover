package br.com.msaulo.mlover.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.msaulo.mlover.api.marvel.character.CharactersListResponse
import br.com.msaulo.mlover.databinding.CharactersLayoutBinding
import coil.load
import coil.size.Scale


class CharactersAdapter(private val dataList: ArrayList<CharactersListResponse.Data.Result>): RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {
    class CharactersViewHolder(val binding: CharactersLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        return CharactersViewHolder(CharactersLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.binding.apply {
            name.text = currentItem.name
            code.text = currentItem.id.toString()
            val thumbnail = "${currentItem.thumbnail.path}.${currentItem.thumbnail.extension}"
                .replace("http://", "https://")
            image.load(thumbnail) {
                crossfade(true)
                // placeholder(R.drawable.)
                scale(Scale.FILL)
            }
            favorite.visibility = View.INVISIBLE

            root.setOnClickListener {
                onItemClickListener?.let {
                    it(currentItem)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private var onItemClickListener: ((CharactersListResponse.Data.Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (CharactersListResponse.Data.Result) -> Unit) {
        onItemClickListener = listener
    }
}

