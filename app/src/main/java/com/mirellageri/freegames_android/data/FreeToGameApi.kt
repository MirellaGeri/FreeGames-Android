package com.mirellageri.freegames_android.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeToGameApi {

    @GET("games")
    suspend fun getGamesList(): List<Game>
    @GET("game")
    suspend fun getGameById(@Query("id")id: Int): Game

    companion object{
        fun getInstance(): FreeToGameApi{
            val retrofit= Retrofit.Builder()
                .baseUrl("https://www.freetogame.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            //le pasamos los planos de la clase y nos devuelve un objeto nuevo
            return retrofit.create(FreeToGameApi::class.java)
        }
    }
}