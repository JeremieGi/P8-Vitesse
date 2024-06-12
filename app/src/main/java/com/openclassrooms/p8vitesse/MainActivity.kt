package com.openclassrooms.p8vitesse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.openclassrooms.p8vitesse.databinding.ActivityMainBinding
import com.openclassrooms.p8vitesse.ui.MainFragment
import com.openclassrooms.p8vitesse.ui.candidate.CandidateDisplayFragment
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

        // Vérifier si le fragment est déjà ouvert
        val existingFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)


        if (existingFragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment()).commit()
        } else {
            // Le fragment est déjà ouvert
            // C'est le cas lors de la rotation de l'écran
        }




    }
}