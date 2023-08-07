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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Patterns
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.monkhoodassignment.databinding.ActivityUpdateUserBinding
import com.example.monkhoodassignment.mvvm.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar

class UpdateUser : AppCompatActivity() {

    private lateinit var binding : ActivityUpdateUserBinding
    private lateinit var pd: ProgressDialog
    private lateinit var vm : ViewModel
    private var UUID : String? = ""
    private lateinit var sharedPreferences : SharedPreferences
    private var uri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore : FirebaseFirestore
    private lateinit var imageBitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        pd = ProgressDialog(this)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        vm = ViewModel()

        UUID = intent.getStringExtra("UUID")

        vm.userFromUserId(UUID){
            uri = Uri.parse(it!!.image)
            Glide.with(this).load(it.image).into(binding.imgProfupdate)
            binding.etNmupdate.setText(it.username)
            binding.etMailupdate.setText(it.email)
            binding.etPhoneupdate.setText(it.phone.toString())
            binding.tvDOBupdate.text = it.age
        }

        binding.imgProfupdate.setOnClickListener {
            selectimgfun()
        }
        val calenderdate = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dateofMonth ->
            calenderdate.set(Calendar.YEAR,year)
            calenderdate.set(Calendar.MONTH,month)
            calenderdate.set(Calendar.DAY_OF_MONTH,dateofMonth)
            updatedate(calenderdate,binding.tvDOBupdate  )
        }
        binding.tvDOBupdate.setOnClickListener {
            DatePickerDialog(
                this,
                datePicker,
                calenderdate.get(Calendar.YEAR),
                calenderdate.get(Calendar.MONTH),
                calenderdate.get(Calendar.DAY_OF_MONTH),
            ).show()

        }
        binding.btnSaveupdate.setOnClickListener {
            if (!validatAllFields()) {return@setOnClickListener}
            updateInSharedPreference()
            uploadImageToFirebaseStorage(imageBitmap)
            finish()
        }
        binding.backupdate.setOnClickListener {
            finish()
        }

    }
    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val storagePath = storageRef.child("Photos/${java.util.UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnSuccessListener { it ->
            val task = it.metadata?.reference?.downloadUrl
            task?.addOnSuccessListener {
                uri = it
                updateInFirebase(uri)
            }
            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateInFirebase(uri: Uri?) {
        val hashMap = hashMapOf<Any, Any>("userId" to UUID!!, "username" to binding.etNmupdate.text.toString(),
            "image" to uri.toString(), "email" to binding.etMailupdate.text.toString(),
            "phone" to binding.etPhoneupdate.text.toString().toLong(),"age" to binding.tvDOBupdate.text.toString())

        firestore.collection("Users").document(UUID!!).set(hashMap)
    }

    private fun updateInSharedPreference() {
        val editor = sharedPreferences.edit()

        val imglink = StoreLocallyAndReturnLink(imageBitmap, UUID)
        val currentUserString = "$UUID, ${binding.etNmupdate.text.toString()},$imglink,${binding.etMailupdate.text.toString()},${binding.etPhoneupdate.text.toString()},${binding.tvDOBupdate.text.toString()}"

        editor.putString(UUID, currentUserString)
        editor.apply()
    }

    private fun validatAllFields(): Boolean {

        if(imageBitmap == null){
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.etNmupdate.text.isEmpty()) {
            Toast.makeText(this, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etNmupdate.requestFocus()
            return false
        }

        if(binding.etMailupdate.text.isEmpty()) {
            Toast.makeText(this, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etMailupdate.requestFocus()
            return false
        }

        if(binding.etPhoneupdate.text.isEmpty()) {
            Toast.makeText(this, "Phone field is Empty!", Toast.LENGTH_SHORT).show()
            binding.etPhoneupdate.requestFocus()
            return false
        }

        if(binding.tvDOBupdate.text.isEmpty()) {
            Toast.makeText(this, "Please choose your DOB!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.etMailupdate.text.toString()).matches()){
            Toast.makeText(this, "Email format is invalid. Please provide a valid email!", Toast.LENGTH_SHORT).show()
            binding.etMailupdate.requestFocus()
            return false
        }

        return true
    }

    private fun selectimgfun() {
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

    private fun pickImageFromGallery() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(pickPictureIntent, 2)
        }
    }
    private fun StoreLocallyAndReturnLink(imgBmp: Bitmap?, UUIDString: String?): String? {
        val directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (directory != null) {
            val file = File(directory, UUIDString + ".jpg")
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(file)
                imgBmp?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
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
                        binding.imgProfupdate.setImageBitmap(imageBitmap)
                    }catch (e: Exception){}
                }
                2 -> {
                    val imageUri = data?.data
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    try {
                        binding.imgProfupdate.setImageBitmap(imageBitmap)
                    }catch (e :Exception){
                    }
                }
            }
        }
    }
}
@SuppressLint("SimpleDateFormat", "SetTextI18n")
private fun updatedate(calenderdate: Calendar, coll_date : TextView) {

    val day = SimpleDateFormat("dd").format(calenderdate.time)
    val month = SimpleDateFormat("MM").format(calenderdate.time)
    val year = SimpleDateFormat("yyyy").format(calenderdate.time)
    coll_date.text = "${day}/${month}/${year}"


}
