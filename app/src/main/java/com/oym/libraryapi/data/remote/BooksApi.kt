package com.oym.libraryapi.data.remote

import com.oym.libraryapi.data.remote.model.BookDTO
import com.oym.libraryapi.data.remote.model.BookDetailDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BooksApi {

    @GET("books")
    fun getBooks(): Call<MutableList<BookDTO>>

    @GET("books/{id}")
    fun getBookDetail(
        @Path("id") id: Int?
    ): Call<BookDetailDTO>
}