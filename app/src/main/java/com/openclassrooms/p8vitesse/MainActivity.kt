package com.openclassrooms.p8vitesse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.openclassrooms.p8vitesse.databinding.ActivityMainBinding
import com.openclassrooms.p8vitesse.ui.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // L'application est mono activity
    // L'activity principale contient un fragment container

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // open the main fragment (list of candidate)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment()).commit()

    }
}