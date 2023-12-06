package com.ko2ic.sample.ui.controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ko2ic.sample.R
import com.ko2ic.sample.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val fragment = ShortMovieFragment().apply {
                arguments = intent.extras
            }
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commitNow()
        }
    }
}
