package br.com.msaulo.mlover.service

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.msaulo.mlover.repository.AppRepository
import br.com.msaulo.mlover.repository.FavoriteCharacter
import br.com.msaulo.mlover.repository.dao.FavoriteCharacterDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class RepositoryServiceUnitTest {
    private lateinit var favoriteCharacterDao: FavoriteCharacterDao
    private lateinit var db: AppRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppRepository::class.java).build()
        favoriteCharacterDao = db.favoriteCharacterDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList(): Unit = runBlocking {
        val favoriteCharacter: FavoriteCharacter = FavoriteCharacter(null, 1, "sample_data")
        favoriteCharacterDao.insert(favoriteCharacter)
        val allFavoriteCharacters = favoriteCharacterDao.getAll()
        val newFavoriteCharacter = allFavoriteCharacters.filter { it.code == favoriteCharacter.code }
        Assert.assertEquals(newFavoriteCharacter[0].code, favoriteCharacter.code)
    }
}