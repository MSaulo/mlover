package br.com.msaulo.mlover.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.msaulo.mlover.R
import br.com.msaulo.mlover.api.marvel.character.CharactersListResponse
import br.com.msaulo.mlover.databinding.CharactersLayoutBinding
import coil.load
import coil.size.Scale


class CharactersPagingAdapter:
    PagingDataAdapter<CharactersListResponse.Data.Result, CharactersPagingAdapter.CharactersViewHolder>(differCallback) {

    class CharactersViewHolder(val binding: CharactersLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        return CharactersViewHolder(CharactersLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val currentItem = getItem(position) as CharactersListResponse.Data.Result
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

            if (currentItem.favorite) {
                favorite.setImageResource(R.drawable.ic_heart_fill)
            }

            root.setOnClickListener {
                onItemClickListener?.let {
                    it(currentItem)
                }
            }

            favorite.setOnClickListener {
                if (currentItem.favorite) {
                    currentItem.favorite = false
                    favorite.setImageResource(R.drawable.ic_heart)
                } else {
                    currentItem.favorite = true
                    favorite.setImageResource(R.drawable.ic_heart_fill)
                }
                onFavoriteClickListener?.let {
                    it(currentItem)
                }
            }
        }
        holder.setIsRecyclable(false)
    }

    private var onItemClickListener: ((CharactersListResponse.Data.Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (CharactersListResponse.Data.Result) -> Unit) {
        onItemClickListener = listener
    }

    private var onFavoriteClickListener: ((CharactersListResponse.Data.Result) -> Unit)? = null

    fun setOnFavoriteClickListener(listener: (CharactersListResponse.Data.Result) -> Unit) {
        onFavoriteClickListener = listener
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<CharactersListResponse.Data.Result>() {
            override fun areItemsTheSame(
                oldItem: CharactersListResponse.Data.Result,
                newItem: CharactersListResponse.Data.Result
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CharactersListResponse.Data.Result,
                newItem: CharactersListResponse.Data.Result
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

