package com.example.nurbk.ps.chatapp.ui.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nurbk.ps.chatapp.R
import com.example.nurbk.ps.chatapp.adapter.UsersGroupAdapter
import com.example.nurbk.ps.chatapp.model.GroupChannel
import com.example.nurbk.ps.chatapp.ui.activity.MainActivity
import com.example.nurbk.ps.chatapp.unit.Constants
import com.example.nurbk.ps.chatapp.viewModel.MainActivityViewModel
import com.vanniktech.rxpermission.RealRxPermission
import kotlinx.android.synthetic.main.group_layout_dialog.view.*
import java.util.*
import kotlin.collections.ArrayList


class CreateGenreDialog : AppCompatDialogFragment(), UsersGroupAdapter
.OnClickItem {

    private val newChatChannel = Constants
        .nameCollection(Constants.COLLECTION_USERS).document()

    val REQUEST_CODE_IMAGE = 111

    private val TAG = "CreateGenreDialog"


    var selectedImage: Uri? = null


    lateinit var viewModel: MainActivityViewModel

    private val uidUsers = ArrayList<String>()
    private val tokenUsers = ArrayList<String>()

    private lateinit var root: View

    private val adapter by lazy {
        UsersGroupAdapter(this)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val builder = AlertDialog.Builder(requireContext())

        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        root = requireActivity().layoutInflater.inflate(
            R.layout.group_layout_dialog
            , null
        )
        builder.setView(root)
        builder.setCancelable(true)
        builder.setTitle(null)

        root.rcChatsGroup.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        root.rcChatsGroup.adapter = adapter
        root.rcChatsGroup.setHasFixedSize(true)

        viewModel.getAllUserLiveData.observe(requireActivity(), Observer { list ->

            adapter.differ.submitList(list)

        })


//        viewModel.profileUserLiveData.observe(requireActivity(), Observer {
//            group.clear()
//
//        })

        root.addImageGroup.setOnClickListener {

            getPermission()

        }

        root.btn_save.setOnClickListener {
            val name = root.et_genre.text.toString()


            if (name.trim().isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Name and photo are required", Toast.LENGTH_LONG
                )
                    .show()
                return@setOnClickListener
            }


            val newChatChannel = Constants.nameCollection("Groups")
                .document(Constants.mAuth.currentUser?.uid.toString())
                .collection(name)
                .document()

//            addGroupChannelChatToUser(newChatChannel.id)

            uidUsers.forEach {
                addChannelChatToMainUser(
                    it,
                    GroupChannel(newChatChannel.id, name, newChatChannel.id, "", tokenUsers)
                )

            }
            addChannelChatToMainUser(
                Constants.mAuth.uid!!,
                GroupChannel(newChatChannel.id, name, newChatChannel.id, "", tokenUsers)
            )
            viewModel.getGroupChannel()
            dismiss()
        }


        return builder.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE) {
            if (resultCode == RESULT_OK) {
                selectedImage = data!!.data
                root.addImageGroup.setImageURI(selectedImage)

            }
        }
    }

    override fun onClickItemUser(select: Boolean, position: Int) {
        if (select) {
            uidUsers.add(adapter.getUserAt(position).uid!!)
            tokenUsers.add(adapter.getUserAt(position).token!!)
        } else {
            uidUsers.remove(adapter.getUserAt(position).uid!!)
            tokenUsers.remove(adapter.getUserAt(position).token!!)
        }
    }


    private fun getPermission() {
        RealRxPermission.getInstance(requireActivity())
            .request(Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe()

        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
            .also {
                startActivityForResult(it, REQUEST_CODE_IMAGE)
            }
    }

    private fun addChannelChatToMainUser(
        currentUser: String,
        groupChannel: GroupChannel
    ) {
        Constants.nameCollection(Constants.COLLECTION_USERS)
            .document(currentUser)
            .collection(Constants.COLLECTION_GROUP_CHANNEL)
            .document(groupChannel.channelId)
            .set(groupChannel)
    }

    private fun addGroupChannelChatToUser(newchannelId: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["test"] = "test 1"
        Constants.nameCollection("Group's")
            .document(newchannelId)
            .collection("Message")
            .add(hashMap)
    }


}



