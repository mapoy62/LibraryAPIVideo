package com.oym.libraryapi.ui


import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.message(message: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(
        requireContext(),
        message,
        duration
    ).show()
}