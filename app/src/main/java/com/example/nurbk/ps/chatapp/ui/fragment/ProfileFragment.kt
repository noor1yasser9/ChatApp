package com.example.nurbk.ps.chatapp.ui.fragmentimport android.content.Intentimport android.os.Bundleimport androidx.fragment.app.Fragmentimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport androidx.appcompat.app.AppCompatActivityimport androidx.lifecycle.Observerimport com.bumptech.glide.Glideimport com.example.nurbk.ps.chatapp.Rimport com.example.nurbk.ps.chatapp.ui.activity.AuthActivityimport com.example.nurbk.ps.chatapp.ui.activity.MainActivityimport com.example.nurbk.ps.chatapp.unit.Constantsimport com.example.nurbk.ps.chatapp.viewModel.MainActivityViewModelimport kotlinx.android.synthetic.main.activity_main.*import kotlinx.android.synthetic.main.fragment_profile.*class ProfileFragment : Fragment() {    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        // Inflate the layout for this fragment        return inflater.inflate(R.layout.fragment_profile, container, false)    }    private lateinit var viewModel: MainActivityViewModel    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {        super.onViewCreated(view, savedInstanceState)        viewModel = (requireActivity() as MainActivity).viewModel        requireActivity().btnImageProfile.visibility = View.GONE        requireActivity().bottomNavigationView.visibility = View.GONE        (activity as AppCompatActivity).supportActionBar!!            .setDisplayHomeAsUpEnabled(true)        requireActivity().titleToolbar.text = "Profile"        requireActivity().toolbarMain.setNavigationOnClickListener {            requireActivity().onBackPressed()        }        viewModel.profileUserLiveData.observe(requireActivity(), Observer {            txtNameProfile.text = it.name            Glide.with(requireActivity())                .load(it.profileImage)                .centerCrop()                .placeholder(R.drawable.ic_account_circle_black_24dp)                .into(ImageProfile)        })        btnSignOutProfile.setOnClickListener {            viewModel.updateUser(                Constants.mAuth.uid!!,                mutableMapOf("token" to "", "statusUser" to 0)            )            sigin()        }    }    private fun sigin() {        Intent(requireActivity(), AuthActivity::class.java).also {            requireActivity().startActivity(it)            requireActivity().finish()        }    }}