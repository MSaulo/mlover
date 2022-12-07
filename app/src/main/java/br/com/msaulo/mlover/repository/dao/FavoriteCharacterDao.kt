package br.com.msaulo.mlover.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.msaulo.mlover.repository.FavoriteCharacter


@Dao
interface FavoriteCharacterDao {
    @Query("SELECT * FROM favorite_character")
    fun getAll(): List<FavoriteCharacter>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteCharacter: FavoriteCharacter)

    @Delete
    suspend fun delete(favoriteCharacter: FavoriteCharacter)

    @Query("DELETE FROM favorite_character WHERE code = :code")
    suspend fun deleteByCode(code: Int)
}