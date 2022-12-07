package br.com.msaulo.mlover.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {
    companion object {
        fun getRetrofitWithClientInstance(path: String, client: okhttp3.OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(path)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}