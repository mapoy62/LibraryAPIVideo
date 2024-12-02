package com.oym.libraryapi.ui.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.oym.libraryapi.R
import com.oym.libraryapi.application.LibraryApp
import com.oym.libraryapi.data.BookRepository
import com.oym.libraryapi.data.remote.model.BookDTO
import com.oym.libraryapi.databinding.FragmentBooksListBinding
import com.oym.libraryapi.ui.MainActivity
import com.oym.libraryapi.ui.adapters.BooksAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.oym.libraryapi.ui.message

class BooksListFragment : Fragment() {

    private var _binding: FragmentBooksListBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var repository: BookRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBooksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Intancia al repositorio
        repository = (requireActivity().application as LibraryApp).repository

        val call : Call<MutableList<BookDTO>> = repository.getBooks()

        call.enqueue(object : Callback<MutableList<BookDTO>> {
            override fun onResponse(
                p0: Call<MutableList<BookDTO>>,
                response: Response<MutableList<BookDTO>>
            ) {
                binding.pbLoading.visibility = View.GONE

                response.body()?.let { books ->
                    binding.rvBooks.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = BooksAdapter(books){ book ->
                            //Reproducir sonido cuando seleccionamos un libro
                            playSound()

                            book.id?.let { id ->
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, BookDetailFragment.newInstance(id))
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }
                }
            }

            override fun onFailure(p0: Call<MutableList<BookDTO>>, p1: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: There is no connection available",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("API_ERROR", "Request failed", p1)
                p1.printStackTrace()

                binding.pbLoading.visibility = View.GONE
            }
        })
    }

    private fun playSound() {
        // Crear un nuevo MediaPlayer para cada reproducción
        val newMediaPlayer = MediaPlayer.create(requireContext(), R.raw.sound_change_page)

        newMediaPlayer.start()

        // Detener y liberar el MediaPlayer después de que se haya reproducido el sonido
        Handler(Looper.getMainLooper()).postDelayed({
            if (newMediaPlayer.isPlaying) {
                newMediaPlayer.stop()
            }
            newMediaPlayer.release() // Liberar los recursos del MediaPlayer
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}