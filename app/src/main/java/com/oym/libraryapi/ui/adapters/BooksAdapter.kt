package com.oym.libraryapi.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oym.libraryapi.data.remote.model.BookDTO
import com.oym.libraryapi.databinding.BookElementBinding

class BooksAdapter(
    private val books: MutableList<BookDTO>,
    private val onBookClicked: (BookDTO) -> Unit
): RecyclerView.Adapter<BookViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = BookElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
        holder.itemView.setOnClickListener{
            onBookClicked(book)
        }
    }
}