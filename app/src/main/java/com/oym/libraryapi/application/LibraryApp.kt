package com.oym.libraryapi.application

import android.app.Application
import com.oym.libraryapi.data.BookRepository
import com.oym.libraryapi.data.remote.RetrofitHelper

class LibraryApp: Application() {
    private val retrofit by lazy {
        RetrofitHelper().getRetrofit()
    }

    val repository by lazy {
        BookRepository(retrofit)
    }
}