package com.mirellageri.freegames_android.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context : Context) {
    private val sharedPreference : SharedPreferences = context.getSharedPreferences("freegame_session",Context.MODE_PRIVATE)
    fun getFavorites(): Set<String>{
        return sharedPreference.getStringSet("FAVORITE_GAMES",emptySet())?.toSet()?:emptySet()
    }
    fun isFavorite(id: String): Boolean{
        return id in getFavorites()
    }
    fun setFavorite(id: String){
         val favorites = getFavorites().toMutableSet()
        favorites.add(id)
        sharedPreference.edit {
            putStringSet("FAVORITE_GAMES",favorites)
        }
    }
    fun removeFavorite(id: String){
        val favorites = getFavorites().toMutableSet()
        favorites.remove(id)
        sharedPreference.edit {
            putStringSet("FAVORITE_GAMES",favorites)
        }
    }
}