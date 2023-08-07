package com.example.monkhoodassignment.mvvm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monkhoodassignment.MyApplication
import com.example.monkhoodassignment.dataclass.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {

    val usersfromfirebase = MutableLiveData<List<User>>()
    val userfromSharedpreference = MutableLiveData<List<User>>()

    val firestore = FirebaseFirestore.getInstance()
    val sharedPreferences = MyApplication.getAppContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)

    fun usersFromFB() : LiveData<List<User>> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("Users")
                    .addSnapshotListener {snapshot, exception ->
                        if (exception!=null)
                            return@addSnapshotListener
                        val userList = snapshot?.documents?.mapNotNull {
                            it.toObject(User::class.java)
                        }
                        usersfromfirebase.postValue(userList!!)
                    }
            } catch (e: Exception) {
                // handle exception
            }
        }
        return usersfromfirebase
    }

    fun delfromFB(UUIDtoDelete : String){

        val collectionRef = firestore.collection("Users")
        val documentRef = collectionRef.document(UUIDtoDelete)    // Get a reference to the specific document

        documentRef.delete()
            .addOnSuccessListener {
                // Document deletion successful
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during deletion
            }
    }

    fun usersFromSp() : LiveData<List<User>> {
        val userList = mutableListOf<User>()

        val allEntries = sharedPreferences.all

        for ((key, value) in allEntries) {
            val userDataArray = (value as String).split(",")
            if (userDataArray.size == 6) {
                val userId = userDataArray[0]
                val username = userDataArray[1]
                val userprofileImage = (userDataArray[2])
                val useremail = userDataArray[3]
                val userphone = userDataArray[4]
                val userdob = userDataArray[5]
                val user = User(userId, username, userprofileImage, useremail, userphone.toLong(), userdob)
                userList.add(user)
            }
        }

        userfromSharedpreference.postValue(userList!!)
        return userfromSharedpreference
    }

    fun delfromSp(UUIDtoDelete : String){
        val editor = sharedPreferences.edit()
        editor.remove(UUIDtoDelete).apply()
        usersFromSp()
    }

    fun userFromUserId(UUID : String?, callback: (User?) -> Unit) {
        var user : User? = null
        firestore.collection("Users").document(UUID!!).get()
        .addOnSuccessListener {document ->
            if (document!=null) {
                user = document.toObject(User::class.java)
                callback(user)
            }
            else {
                callback(null)
            }
        }
    }

}