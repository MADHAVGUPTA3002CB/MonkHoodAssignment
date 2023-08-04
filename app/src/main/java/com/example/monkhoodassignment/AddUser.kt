package com.example.monkhoodassignment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.monkhoodassignment.databinding.ActivityAddUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calenderdate = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dateofMonth ->
            calenderdate.set(Calendar.YEAR,year)
            calenderdate.set(Calendar.MONTH,month)
            calenderdate.set(Calendar.DAY_OF_MONTH,dateofMonth)
            updatedatelabel(calenderdate,binding.tvDOB  )
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
            showImageSelectionOptionDialog()
        }

        binding.btnSave.setOnClickListener {
            uploadPostImageToFirebaseStorage(imageBitmap)
        }

    }
    private fun showImageSelectionOptionDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_select_image_options)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()

        }

        dialog.findViewById<LinearLayout>(R.id.layoutTakePicture).setOnClickListener {
            takePhotoWithCamera()
            dialog.dismiss()
        }

        dialog.findViewById<ConstraintLayout>(R.id.layoutSelectFromGallery).setOnClickListener {
            pickImageFromGallery()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
             //See description at declaration
        }

        dialog.show()
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(pickPictureIntent, 2)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, 1)
    }

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
    private fun uploadPostImageToFirebaseStorage(imageBitmap: Bitmap?) {
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
                postImagePost(uri)
            }
            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun postImagePost(uri: Uri?) {

        Toast.makeText(this,"i am here",Toast.LENGTH_SHORT).show()

        val hashMap = hashMapOf<Any, Any>("image" to uri.toString(), "userid" to userId,
            "username" to binding.etNm.text.toString(), "useremail" to binding.etMail.text.toString(),
            "userphone" to binding.etPhone.text.toString(),"DOB" to binding.tvDOB.text.toString())

        firestore.collection("Users").document(userId).set(hashMap)

        Toast.makeText(this,"dal gya?",Toast.LENGTH_SHORT).show()
    }
}
@SuppressLint("SimpleDateFormat", "SetTextI18n")
private fun updatedatelabel(calenderdate: Calendar, coll_date : TextView) {

    val day = SimpleDateFormat("dd").format(calenderdate.time)
    val month = SimpleDateFormat("MM").format(calenderdate.time)
    val year = SimpleDateFormat("yyyy").format(calenderdate.time)
    coll_date.text = "${day}/${month}/${year}"


}
