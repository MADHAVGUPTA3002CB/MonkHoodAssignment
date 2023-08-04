package com.example.monkhoodassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monkhoodassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.bottomNav
        val firebase_frag = FirebaseFragment()
        val sharedPref_frag = SharedPreferenceFragment()

    }
}