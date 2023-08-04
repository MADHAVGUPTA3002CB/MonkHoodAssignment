package com.example.monkhoodassignment

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monkhoodassignment.databinding.ActivityAddUserBinding

class AddUser : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private lateinit var pd: ProgressDialog

    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private var uri: Uri? = null
    private lateinit var firestore : FirebaseFirestore

    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}