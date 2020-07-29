package com.example.nurbk.ps.chatapp.ui.fragmentimport android.app.ProgressDialogimport android.content.Intentimport android.os.Bundleimport android.text.Editableimport android.text.TextWatcherimport android.util.Patternsimport androidx.fragment.app.Fragmentimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.Toastimport androidx.lifecycle.Observerimport androidx.navigation.NavControllerimport androidx.navigation.Navigationimport com.example.nurbk.ps.chatapp.Rimport com.example.nurbk.ps.chatapp.ui.activity.AuthActivityimport com.example.nurbk.ps.chatapp.ui.activity.MainActivityimport com.example.nurbk.ps.chatapp.unit.Constantsimport com.example.nurbk.ps.chatapp.viewModel.AuthActivityViewModelimport kotlinx.android.synthetic.main.fragment_sign_in.*class SignInFragment : Fragment(), TextWatcher {    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        // Inflate the layout for this fragment        return inflater.inflate(R.layout.fragment_sign_in, container, false)    }    private lateinit var viewModel: AuthActivityViewModel    private lateinit var navigationController: NavController    private lateinit var progressDialog: ProgressDialog    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {        super.onViewCreated(view, savedInstanceState)        viewModel = (requireActivity() as AuthActivity).viewModel        navigationController = Navigation.findNavController(view)        progressDialog = ProgressDialog(requireActivity())        viewModel.isSignInLiveData.observe(requireActivity(), Observer {            progressDialog.dismiss()            if (it) {                sigin()            } else {                Toast.makeText(                    requireActivity(),                    "Email or password are not correct",                    Toast.LENGTH_LONG                ).show()            }        })        btnCreateNewAccount.setOnClickListener {            navigationController.navigate(R.id.action_signInFragment_to_signUpFragment)        }        txtEmailSignIn.addTextChangedListener(this)        txtPasswordSignIn.addTextChangedListener(this)        btnSignIn.setOnClickListener {            val email = txtEmailSignIn.text.trim().toString()            val password = txtPasswordSignIn.text.trim().toString()            if (email.isEmpty()) {                txtEmailSignIn.error = "Name Required"                txtEmailSignIn.requestFocus()                return@setOnClickListener            }            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {                txtEmailSignIn.error = "Please Enter a Valid Email"                txtEmailSignIn.requestFocus()                return@setOnClickListener            }            if (password.length < 6) {                txtPasswordSignIn.error = "6 Char Required"                txtPasswordSignIn.requestFocus()                return@setOnClickListener            }            progressDialog.setMessage("Login...")            progressDialog.setCancelable(false)            progressDialog.show()            viewModel.signIn(email, password)        }    }    override fun onStart() {        super.onStart()        verifyEmailAddress()    }    private fun sigin() {        Intent(requireActivity(), MainActivity::class.java).also {            requireActivity().startActivity(it)            requireActivity().finish()        }    }    private fun verifyEmailAddress() {        val user = Constants.mAuth.currentUser        if (user?.uid != null) {            if (user.isEmailVerified) {                sigin()            } else {                Toast.makeText(                    requireActivity(),                    "Please Verify Your Account...",                    Toast.LENGTH_LONG                )                    .show()            }        }    }    override fun afterTextChanged(s: Editable?) {    }    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {    }    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {        btnSignIn.isEnabled = txtEmailSignIn.text.trim().toString().isNotEmpty() &&                txtPasswordSignIn.text.trim().toString().isNotEmpty()    }}