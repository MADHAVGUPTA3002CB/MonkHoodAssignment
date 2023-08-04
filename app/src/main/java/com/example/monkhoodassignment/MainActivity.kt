package com.example.monkhoodassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.monkhoodassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnadd.setOnClickListener {
            startActivity(Intent(this,AddUser::class.java))
        }
        val navView = binding.bottomNav
        val firebase_frag = FirebaseFragment()
        val sharedPref_frag = SharedPreferenceFragment()

        setFragment(firebase_frag)

        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.firebase -> {

                    setFragment(firebase_frag)
                }
                R.id.SharedReference -> {

                    setFragment(sharedPref_frag)
                }


            }
            true
        }

    }
    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, fragment)
            commit()
        }
    }
}