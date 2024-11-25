package com.oym.libraryapi.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.oym.libraryapi.R
import com.oym.libraryapi.application.LibraryApp
import com.oym.libraryapi.data.BookRepository
import com.oym.libraryapi.data.remote.model.BookDetailDTO
import com.oym.libraryapi.databinding.FragmentBookDetailBinding
import com.oym.libraryapi.utils.Constants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val BOOK_ID = "book_id"

class BookDetailFragment : Fragment() {

    private var bookId: Int? = null

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: BookRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            bookId = args.getInt(BOOK_ID)
            Log.d(Constants.LOGTAG, "Id recibido $bookId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Obteniendi la instancia al repositorio
        repository = (requireActivity().application as LibraryApp).repository

        bookId?.let { id ->
            //Llamada al endpoint para consumir los detalles del libro
            val call: Call<BookDetailDTO> = repository.getBookDetails(id)

            call.enqueue(object : Callback<BookDetailDTO>{
                override fun onResponse(p0: Call<BookDetailDTO>, response: Response<BookDetailDTO>) {
                    binding.apply {
                        pbLoading.visibility = View.GONE

                        //Utilizando la respuesta exitosa, asignamos los valores a las vistas
                        Glide.with(requireActivity())
                            .load(response.body()?.image)
                            .into(ivImage)

                        tvTitle.text = response.body()?.title
                        tvLanguage.text = getString(R.string.idioma, response.body()?.language ?: getString(R.string.NA))
                        tvPublisher.text =
                            getString(R.string.editorial, response.body()?.publisher ?: getString(R.string.NA))
                        tvPublicationDate.text = getString(R.string.publicacion ,response.body()?.publication ?: getString(R.string.NA))
                        tvLongDesc.text = response.body()?.description

                        //Configuración paara reproducir el video de Yoitube
                        val videoId = response.body()?.videoId
                        if (!videoId.isNullOrEmpty()){
                            binding.vvBook.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.cueVideo(videoId, 0f)
                                }
                            })

                            // Vincular el ciclo de vida del reproductor al del fragmento
                            lifecycle.addObserver(binding.vvBook)
                        } else {
                            binding.vvBook.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(p0: Call<BookDetailDTO>, p1: Throwable) {
                    //Manejando los errores de conexión
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vvBook.release()
        _binding = null
    }

    companion object{
        @JvmStatic
        fun newInstance(bookId: Int) =
            BookDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(BOOK_ID, bookId)
                }
            }
    }
}