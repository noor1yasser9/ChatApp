package com.example.nurbk.ps.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nurbk.ps.chatapp.R
import com.example.nurbk.ps.chatapp.model.GroupChannel
import com.example.nurbk.ps.chatapp.model.User
import kotlinx.android.synthetic.main.list_item_group.view.*
import kotlinx.android.synthetic.main.list_item_users.view.*
import kotlinx.android.synthetic.main.list_item_users_group.view.*


class UsersGroupAdapter(val onclick: OnClickItem) :
    RecyclerView.Adapter<UsersGroupAdapter.UsersGroupViewHolder>() {

    inner class UsersGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersGroupViewHolder {
        return UsersGroupViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_users_group, parent, false)
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: UsersGroupViewHolder, position: Int) {
        val user = getUserAt(position)

        holder.itemView.apply {

            btnSelect.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    btnSelect.setBackgroundResource(R.drawable.bg_btn_select_interests)

                } else {
                    btnSelect.setBackgroundResource(R.drawable.bg_btn_interests)
                }


                onclick.onClickItemUser(isChecked, position)

            }

            nameUserChatG.text = user.name


            Glide.with(holder.itemView.context)
                .load(user.profileImage)
                .centerCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(imgUserChatG)
            setOnClickListener {
                onItemClickListener?.let {
                    it(user)
                }
            }
        }

    }

    interface OnClickItem {
        fun onClickItemUser(select: Boolean, position: Int)
    }
}