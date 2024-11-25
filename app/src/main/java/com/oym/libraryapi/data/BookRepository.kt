package com.oym.libraryapi.data

import com.oym.libraryapi.data.remote.BooksApi
import com.oym.libraryapi.data.remote.model.BookDTO
import com.oym.libraryapi.data.remote.model.BookDetailDTO
import retrofit2.Call
import retrofit2.Retrofit

class BookRepository(
    private val retrofit: Retrofit
) {
    private val booksApi: BooksApi = retrofit.create(BooksApi::class.java)

    fun getBooks(): Call<MutableList<BookDTO>> = booksApi.getBooks()

    fun getBookDetails(id: Int?): Call<BookDetailDTO> =
        booksApi.getBookDetail(id)
}