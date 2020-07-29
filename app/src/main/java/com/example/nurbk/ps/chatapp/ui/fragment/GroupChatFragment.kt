package com.example.nurbk.ps.chatapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nurbk.ps.chatapp.R
import com.example.nurbk.ps.chatapp.adapter.GroupsAdapter
import com.example.nurbk.ps.chatapp.adapter.UsersAdapter
import com.example.nurbk.ps.chatapp.ui.activity.MainActivity
import com.example.nurbk.ps.chatapp.viewModel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_group_chat.*
import kotlinx.android.synthetic.main.fragment_list_chat.*
import kotlinx.android.synthetic.main.group_layout_dialog.*


class GroupChatFragment : Fragment() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var navController: NavController
    private val adapter by lazy {
        GroupsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as MainActivity).viewModel
        navController = Navigation.findNavController(view)

        requireActivity().btnImageProfile.visibility = View.VISIBLE
        requireActivity().bottomNavigationView.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)
        requireActivity().titleToolbar.text = "Chat Groups"


        viewModel.getChatGroupChannel.observe(requireActivity(), Observer { list ->

            adapter.differ.submitList(list)
        })


        adapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("group", it)
            bundle.putString("type", "group")

            navController.navigate(R.id.action_groupChatFragment_to_chatFragment, bundle)

        }

        rcGroupChats.layoutManager = GridLayoutManager(requireContext(), 3)
        rcGroupChats.adapter = adapter
        rcGroupChats.setHasFixedSize(true)


    }

}