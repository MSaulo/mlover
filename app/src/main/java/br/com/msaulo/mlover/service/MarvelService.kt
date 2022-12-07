package br.com.msaulo.mlover.service

import br.com.msaulo.mlover.api.marvel.MarvelCharacterInterface
import br.com.msaulo.mlover.util.NetworkUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit


class MarvelService() {
    private val api: Retrofit by lazy {
        NetworkUtils.getRetrofitWithClientInstance(
            "https://gateway.marvel.com",
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Origin", "https://developer.marvel.com")
                        .addHeader("Referer", "https://developer.marvel.com/")
                        .build()

                    chain.proceed(request)
                }
                .addInterceptor { chain ->
                    val url = chain.request().url.newBuilder()
                        .addQueryParameter("apikey", "5d3062cdccc183f0771df4d6e37e0bcf")
                        .build()

                    chain.proceed(chain.request().newBuilder().url(url).build())
                }
                .build()
        )
    }

    val character: MarvelCharacterInterface by lazy {
        api.create(MarvelCharacterInterface::class.java)
    }
}