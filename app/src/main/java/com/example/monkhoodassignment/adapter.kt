package com.example.monkhoodassignment.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.monkhoodassignment.UpdateUser
import com.example.monkhoodassignment.databinding.ItemBinding
import com.example.monkhoodassignment.dataclass.User
import com.example.monkhoodassignment.mvvm.ViewModel

class adapter: RecyclerView.Adapter<UserViewHolder>() {

    val vm = ViewModel()
    var alluserslist = listOf<User>()
    lateinit var binding : ItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alluserslist.size
    }

    @SuppressLint("CommitPrefEdits")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentuser = alluserslist[position]
        Glide.with(holder.itemView.context).load(currentuser.image).signature(ObjectKey(System.currentTimeMillis())).into(holder.image)

        holder.Name.text = currentuser.username
        holder.Mail.text = currentuser.email
        holder.Phone.text = currentuser.phone.toString()
        holder.age.text = currentuser.age
        //holder.imgEdit.listener : intent launch, only pass user id
        holder.imgtoEdit.setOnClickListener {
            val intent = Intent(holder.itemView.context,UpdateUser::class.java)
            intent.putExtra("UUID",currentuser.userId)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgtoDel.setOnClickListener {

            vm.delfromFB(currentuser.userId!!)
            vm.delfromSp(currentuser.userId!!)
            alluserslist = alluserslist.filterNot { it == alluserslist[position] }
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, alluserslist.size)

        }

    }

    fun setUserList(list: List<User>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(alluserslist, list))
        alluserslist = list
        diffResult.dispatchUpdatesTo(this)
    }

}

class UserViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val image: ImageView = binding.imgProf
    val imgtoEdit : ImageView = binding.imgEdit
    val imgtoDel : ImageView = binding.imgDel
    val Name: TextView = binding.tvName
    val Mail: TextView = binding.tvMail
    val Phone: TextView = binding.tvPhone
    val age: TextView = binding.tvDOB
}

class MyDiffCallback(
    private val oldList : List<User>,
    private val newList : List<User>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}