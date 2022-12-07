package br.com.msaulo.mlover.service

import br.com.msaulo.mlover.api.marvel.MarvelCharacterInterface
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MarvelServiceUnitTest {
    @Test
    fun `should get character list`(): Unit = runBlocking {
        val server = MockWebServer()
        server.start()
        val mockedResponse = readJson("response_character_list_by_name.json")
        server.enqueue(MockResponse().setBody(mockedResponse))

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: MarvelCharacterInterface = retrofit.create(MarvelCharacterInterface::class.java)
        val res = api.getCharactersList(0)

        val request = server.takeRequest()
        server.shutdown()

        Assert.assertEquals(request.path, "/v1/public/characters?offset=0&limit=20")
        Assert.assertEquals(res.body()!!.code, 200)
        Assert.assertEquals(res.body()!!.data.results.isNotEmpty(), true)
    }

    @Test
    fun `should get character list by name`(): Unit = runBlocking {
        val server = MockWebServer()
        server.start()
        val mockedResponse = readJson("response_character_list.json")
        server.enqueue(MockResponse().setBody(mockedResponse))

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: MarvelCharacterInterface = retrofit.create(MarvelCharacterInterface::class.java)
        val res = api.getCharactersListByName("thor", 0)

        val request = server.takeRequest()
        server.shutdown()

        Assert.assertEquals(request.path, "/v1/public/characters?nameStartsWith=thor&offset=0&limit=20")
        Assert.assertEquals(res.body()!!.code, 200)
        Assert.assertEquals(res.body()!!.data.results.isNotEmpty(), true)
    }

    @Test
    fun `should get empty character list by name`(): Unit = runBlocking {
        val server = MockWebServer()
        server.start()
        val mockedResponse = readJson("response_character_list_by_name_empty.json")
        server.enqueue(MockResponse().setBody(mockedResponse))

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: MarvelCharacterInterface = retrofit.create(MarvelCharacterInterface::class.java)
        val res = api.getCharactersListByName("flash", 0)

        val request = server.takeRequest()
        server.shutdown()

        Assert.assertEquals(request.path, "/v1/public/characters?nameStartsWith=flash&offset=0&limit=20")
        Assert.assertEquals(res.body()!!.code, 200)
        Assert.assertEquals(res.body()!!.data.results.isEmpty(), true)
    }

    @Test
    fun `should get character`(): Unit = runBlocking {
        val server = MockWebServer()
        server.start()
        val mockedResponse = readJson("response_character.json")
        server.enqueue(MockResponse().setBody(mockedResponse))

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api: MarvelCharacterInterface = retrofit.create(MarvelCharacterInterface::class.java)
        val res = api.getCharacter(1009664)

        val request = server.takeRequest()
        server.shutdown()

        Assert.assertEquals(request.path, "/v1/public/characters/1009664")
        Assert.assertEquals(res.body()!!.code, 200)
        Assert.assertEquals(res.body()!!.data.results[0].id, 1009664)
    }

    @Throws(IOException::class)
    fun readJson(fileName: String): String {
        var inputStream: InputStream? = null
        try {
            inputStream =
                javaClass.classLoader?.getResourceAsStream(fileName)
            val builder = StringBuilder()
            val reader = BufferedReader(InputStreamReader(inputStream))

            var str: String? = reader.readLine()
            while (str != null) {
                builder.append(str)
                str = reader.readLine()
            }
            return builder.toString()
        } finally {
            inputStream?.close()
        }
    }
}