package com.example.nurbk.ps.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nurbk.ps.chatapp.R
import com.example.nurbk.ps.chatapp.model.User
import kotlinx.android.synthetic.main.list_item_users.view.*


class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback =
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }


    val differ = AsyncListDiffer(this, differCallback)

    fun getUserAt(position: Int): User {
        return differ.currentList[position]
    }


    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_users, parent, false)
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getUserAt(position)

        holder.itemView.apply {

            nameUserChat.text = user.name
            timeUserChat.text = android.text.format.DateFormat.format(
                "hh:mm a",
                user.date
            )


            if (user.statusUser == 1) {
                statusUser.visibility = View.VISIBLE
            } else {
                statusUser.visibility = View.GONE
            }

            messageUserChat.text = user.lastMessage

            Glide.with(holder.itemView.context)
                .load(user.profileImage)
                .centerCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(imgUserChat)
            setOnClickListener {
                onItemClickListener?.let { it(user) }
            }
        }

    }
}