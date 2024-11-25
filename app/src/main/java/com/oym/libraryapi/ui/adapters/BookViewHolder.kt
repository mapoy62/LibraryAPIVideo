package com.oym.libraryapi.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oym.libraryapi.data.remote.model.BookDTO
import com.oym.libraryapi.databinding.BookElementBinding

class BookViewHolder(
    private val binding: BookElementBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(book: BookDTO){
        binding.tvTitle.text = book.title
        binding.tvAuthor.text = book.author
        binding.tvYear.text = book.year.toString()
        binding.tvGenre.text = book.genre

        Glide.with(binding.root.context)
            .load(book.image)
            .into(binding.ivThumbnail)
    }
}