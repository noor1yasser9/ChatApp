package com.example.nurbk.ps.chatapp.adapter

import android.util.Log
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


class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>() {

    inner class GroupsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback =
        object : DiffUtil.ItemCallback<GroupChannel>() {
            override fun areItemsTheSame(oldItem: GroupChannel, newItem: GroupChannel): Boolean {
                return oldItem.channelId == newItem.channelId
            }

            override fun areContentsTheSame(oldItem: GroupChannel, newItem: GroupChannel): Boolean {
                return oldItem == newItem
            }
        }


    val differ = AsyncListDiffer(this, differCallback)

    fun getGroupAt(position: Int): GroupChannel {
        return differ.currentList[position]
    }


    private var onItemClickListener: ((GroupChannel) -> Unit)? = null

    fun setOnItemClickListener(listener: (GroupChannel) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        return GroupsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_group, parent, false)
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val group = getGroupAt(position)

        holder.itemView.apply {

            nameGroup.text = group.group_name

//            Glide.with(holder.itemView.context)
//                .load(group.photo)
//                .centerCrop()
//                .placeholder(R.drawable.ic_account_circle_black_24dp)
//                .into(imgUserChat)

            setOnClickListener {
                onItemClickListener?.let {
                    it(group)
                }
            }
        }

    }
}