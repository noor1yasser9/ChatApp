package com.example.nurbk.ps.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nurbk.ps.chatapp.R
import com.example.nurbk.ps.chatapp.model.MessageType
import com.example.nurbk.ps.chatapp.model.TextMessage
import com.example.nurbk.ps.chatapp.unit.Constants
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_design_chat.view.*
import kotlinx.android.synthetic.main.item_design_chat_end.view.*
import kotlinx.android.synthetic.main.recipient_item_image_message.view.*
import kotlinx.android.synthetic.main.sender_item_image_message.view.*

class MessageAdapter(

) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {


    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun getItemViewType(position: Int): Int {
        val textMessage = getTextMessageAt(position)

        return if (textMessage.type == MessageType.TEXT) {
            if (textMessage.senderId == Constants.mAuth.currentUser?.uid) {
                R.layout.item_design_chat
            } else {
                R.layout.item_design_chat_end
            }
        } else {
            if (textMessage.senderId == Constants.mAuth.currentUser?.uid) {
                R.layout.sender_item_image_message
            } else {
                R.layout.recipient_item_image_message
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val textMessage = getTextMessageAt(position)
        holder.itemView.apply {


            if (textMessage.type == MessageType.TEXT) {
                if (textMessage.senderId == Constants.mAuth.currentUser?.uid) {
                    txtTime.text =
                        android.text.format.DateFormat.format("hh:mm a", textMessage.date)
                    txtMessage.text = textMessage.text
                } else {
                    txtTimeEnd.text =
                        android.text.format.DateFormat.format("hh:mm a", textMessage.date)
                    txtMessageEnd.text = textMessage.text
                }
            } else {
                val imageView: ImageView
                val textView: TextView
                if (textMessage.senderId == Constants
                        .mAuth.currentUser?.uid
                ) {
                    textView = txt_sender_time_message
                    imageView = image_sender_message
                } else {
                    textView = txt_recipient_time_message
                    imageView = image_recipient_message
                }
                textView.text = android.text.format.DateFormat.format("hh:mm a", textMessage.date)
                Glide.with(holder.itemView.context)
                    .load(textMessage.text)
                    .placeholder(R.drawable.ic_image)
                    .into(imageView)
            }
        }

    }

    private val differCallback =
        object : DiffUtil.ItemCallback<TextMessage>() {
            override fun areItemsTheSame(oldItem: TextMessage, newItem: TextMessage): Boolean {
                return oldItem.date == newItem.date &&
                        oldItem.text == newItem.text
            }

            override fun areContentsTheSame(oldItem: TextMessage, newItem: TextMessage): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    private fun getTextMessageAt(position: Int): TextMessage {
        return differ.currentList[position]
    }
}