package com.mirellageri.freegames_android.data

import com.google.gson.annotations.SerializedName

data class Game (
    //ayuda mucha cuando en un proyecto de emoresa lo quieran ofuscar para que no entiendan su codigo y asi no afecte a tus variables declaradas
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("short_description") val shortDescription:String,
    @SerializedName("description") val description:String?,
    @SerializedName("genre") val genre: String,
    @SerializedName("platform") val platform : String,
    @SerializedName("freetogame_profile_url") val profileUrl: String,
    @SerializedName("developer") val developer: String,
    @SerializedName("game_url") val gameUrl : String,
    @SerializedName("screenshots") val screenshots : List<Screenshot>?,
    @SerializedName("minimum_system_requirements") val systemRequirements: SystemRequirements?
)
data class Screenshot(
    @SerializedName("image") val image : String
)
data class SystemRequirements(
    @SerializedName("os") val os: String,
    @SerializedName("processor") val processor :String,
    @SerializedName("memory") val memory :String,
    @SerializedName("graphics") val graphics: String,
    @SerializedName("storage") val storage : String
)
