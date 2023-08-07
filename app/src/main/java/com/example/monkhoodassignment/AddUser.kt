package com.example.monkhoodassignment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.monkhoodassignment.databinding.ActivityAddUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID
import kotlin.io.encoding.ExperimentalEncodingApi


class AddUser : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private lateinit var pd: ProgressDialog
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private var uri: Uri? = null
    private lateinit var firestore : FirebaseFirestore
    private lateinit var bitmap: Bitmap

    var userId : String = ""

    private lateinit var imageBitmap : Bitmap
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calenderdate = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dateofMonth ->
            calenderdate.set(Calendar.YEAR,year)
            calenderdate.set(Calendar.MONTH,month)
            calenderdate.set(Calendar.DAY_OF_MONTH,dateofMonth)
            updatedate(calenderdate,binding.tvDOB  )
        }
        binding.tvDOB.setOnClickListener {
            DatePickerDialog(
                this,
                datePicker,
                calenderdate.get(Calendar.YEAR),
                calenderdate.get(Calendar.MONTH),
                calenderdate.get(Calendar.DAY_OF_MONTH),
            ).show()
        }



        userId = UUID.randomUUID().toString()
        pd = ProgressDialog(this)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        binding.imgProf.setOnClickListener {
            imageOptionDialogue()
        }

        binding.btnSave.setOnClickListener {
            if (!checkallfields()) {return@setOnClickListener}
            addImagetoFB(imageBitmap)

            postImageToSP(imageBitmap)

        }
        binding.imgforback.setOnClickListener {
            finish()
        }

    }

    private fun checkallfields(): Boolean {

        if(imageBitmap == null){
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.etNm.text.isEmpty()) {
            Toast.makeText(this, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etNm.requestFocus()
            return false
        }

        if(binding.etMail.text.isEmpty()) {
            Toast.makeText(this, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etMail.requestFocus()
            return false
        }

        if(binding.etPhone.text.isEmpty()) {
            Toast.makeText(this, "Phone field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etPhone.requestFocus()
            return false
        }

        if(binding.tvDOB.text.isEmpty()) {
            Toast.makeText(this, "Please choose your DOB!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.etMail.text.toString()).matches()){
            Toast.makeText(this, "Email format is invalid. Please provide a valid email!", Toast.LENGTH_SHORT).show()
            binding.etMail.requestFocus()
            return false
        }

        return true
    }

    private fun postImageToSP(imageBitmap: Bitmap) {

//        Toast.makeText(this,"yaha to aa gya mai",Toast.LENGTH_SHORT).show()
        val linkOfImg = addToLocal(imageBitmap,userId)

        sharedPreferences = this.getSharedPreferences("user_data", Context.MODE_PRIVATE)
//        Toast.makeText(this,"shared preference tyar",Toast.LENGTH_SHORT).show()
//        val encodedImage: String = encodeBitmapToBase64(imageBitmap)
//        Log.d("hmra image",encodedImage)
//        Toast.makeText(this, encodedImage, Toast.LENGTH_SHORT).show()
        val userDataString = "$userId, ${binding.etNm.text.toString()},$linkOfImg,${binding.etMail.text.toString()},${binding.etPhone.text.toString()},${binding.tvDOB.text.toString()}"
        val editor = sharedPreferences.edit()
        editor.putString(userId, userDataString)

//        Toast.makeText(this, userDataString, Toast.LENGTH_SHORT).show()
        editor.apply()

    }

    private fun addToLocal(imageBitmap: Bitmap, userId: String): String? {
        val directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (directory != null) {
            val file = File(directory, "$userId.jpg")
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(file)
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                return file.path
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    outputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }


    @OptIn(ExperimentalEncodingApi::class)
    private fun encodeBitmapToBase64(imageBitmap: Bitmap): String {

        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return (Base64.encodeToString(byteArray, Base64.DEFAULT) + "alookachaloo")
    }

    private fun imageOptionDialogue() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_select_image_options)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()

        }

        dialog.findViewById<LinearLayout>(R.id.layoutTakePicture).setOnClickListener {
            fromcamera()
            dialog.dismiss()
        }

        dialog.findViewById<ConstraintLayout>(R.id.layoutSelectFromGallery).setOnClickListener {
            pickFromGallery()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
             //See description at declaration
        }

        dialog.show()
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun pickFromGallery() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(pickPictureIntent, 2)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun fromcamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    imageBitmap = data?.extras?.get("data") as Bitmap
                    try {
                        binding.imgProf.setImageBitmap(imageBitmap)
                    }catch (e: Exception){}
                }
                2 -> {
                    val imageUri = data?.data
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    try {
                        binding.imgProf.setImageBitmap(imageBitmap)
                    }catch (e :Exception){
                    }
                }
            }
        }
    }
    private fun addImagetoFB(imageBitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        bitmap = imageBitmap!!

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnSuccessListener { it ->
            val task = it.metadata?.reference?.downloadUrl
            task?.addOnSuccessListener {
                uri = it
                addDataToFirebase(uri)
            }
            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addDataToFirebase(uri: Uri?) {

//        Toast.makeText(this,"i am here",Toast.LENGTH_SHORT).show()

        val hashMap = hashMapOf<Any, Any>("image" to uri.toString(), "userId" to userId,
            "username" to binding.etNm.text.toString(), "email" to binding.etMail.text.toString(),
            "phone" to binding.etPhone.text.toString().toLong(),"age" to binding.tvDOB.text.toString())

        firestore.collection("Users").document(userId).set(hashMap)

//        Toast.makeText(this,"dal gya?",Toast.LENGTH_SHORT).show()
    }
}
@SuppressLint("SimpleDateFormat", "SetTextI18n")
private fun updatedate(calenderdate: Calendar, coll_date : TextView) {

    val day = SimpleDateFormat("dd").format(calenderdate.time)
    val month = SimpleDateFormat("MM").format(calenderdate.time)
    val year = SimpleDateFormat("yyyy").format(calenderdate.time)
    coll_date.text = "${day}/${month}/${year}"


}
