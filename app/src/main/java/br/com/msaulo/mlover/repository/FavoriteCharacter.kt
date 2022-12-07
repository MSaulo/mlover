package br.com.msaulo.mlover.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_character")
data class FavoriteCharacter(
     @PrimaryKey(autoGenerate = true) val id: Int?,
     @ColumnInfo(name = "code") val code: Int?,
     @ColumnInfo(name = "data") val data: String?
)