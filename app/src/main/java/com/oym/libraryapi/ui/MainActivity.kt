package com.oym.libraryapi.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.oym.libraryapi.R
import com.oym.libraryapi.databinding.ActivityMainBinding
import com.oym.libraryapi.ui.fragments.BooksListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //Mostrando el fragment inicial de Book_List_Fragment
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BooksListFragment())
                .commit()
        }
    }
}