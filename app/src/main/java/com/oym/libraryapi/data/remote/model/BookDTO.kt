package com.oym.libraryapi.data.remote.model

import com.google.gson.annotations.SerializedName

data class BookDTO (

    @SerializedName("id")
    var id: Int? = 0,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("author")
    var author: String? = null,

    @SerializedName("year")
    var year: Int? = 0,

    @SerializedName("genre")
    var genre: String? = null,

    @SerializedName("imageUrl")
    var image: String? = null

)