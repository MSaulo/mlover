package br.com.msaulo.mlover.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.msaulo.mlover.repository.dao.FavoriteCharacterDao


@Database(entities = [FavoriteCharacter::class], version = 1)
abstract class AppRepository: RoomDatabase() {
    abstract fun favoriteCharacterDao(): FavoriteCharacterDao
}