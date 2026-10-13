package com.mirellageri.freegames_android.data

import com.google.gson.annotations.SerializedName

data class Game (
    //ayuda mucha cuando en un proyecto de emoresa lo quieran ofuscar para que no entiendan su codigo y asi no afecte a tus variables declaradas
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String
)