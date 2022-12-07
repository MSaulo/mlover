package br.com.msaulo.mlover.service

import android.content.Context
import androidx.room.Room
import br.com.msaulo.mlover.repository.AppRepository
import br.com.msaulo.mlover.repository.dao.FavoriteCharacterDao


class RepositoryService(context: Context) {
    private val repository: AppRepository by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppRepository::class.java,
            "mlover.db")
            .allowMainThreadQueries()
            .build()
    }

    val favoriteCharacter:  FavoriteCharacterDao by lazy {
        repository.favoriteCharacterDao()
    }
}