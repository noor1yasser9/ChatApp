package com.example.nurbk.ps.chatapp.ui.fragmentimport android.Manifestimport android.app.Activityimport android.content.Contextimport android.content.Intentimport android.content.pm.PackageManagerimport android.graphics.Bitmapimport android.os.Buildimport android.os.Bundleimport android.provider.MediaStoreimport android.text.Editableimport android.text.TextWatcherimport android.util.Logimport android.view.*import androidx.fragment.app.Fragmentimport androidx.appcompat.app.AppCompatActivityimport androidx.core.app.ActivityCompatimport com.bumptech.glide.Glideimport com.example.nurbk.ps.chatapp.Rimport com.example.nurbk.ps.chatapp.adapter.MessageAdapterimport com.example.nurbk.ps.chatapp.api.RetrofitInstanceimport com.example.nurbk.ps.chatapp.model.*import com.example.nurbk.ps.chatapp.service.FirebaseServiceimport com.example.nurbk.ps.chatapp.ui.activity.MainActivityimport com.example.nurbk.ps.chatapp.unit.Constantsimport com.example.nurbk.ps.chatapp.viewModel.MainActivityViewModelimport com.google.firebase.iid.FirebaseInstanceIdimport kotlinx.android.synthetic.main.activity_main.*import kotlinx.android.synthetic.main.fragment_chat.*import kotlinx.android.synthetic.main.fragment_chat.view.*import kotlinx.coroutines.CoroutineScopeimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.launchimport java.io.ByteArrayOutputStreamimport java.lang.Exceptionimport java.util.*class ChatFragment : Fragment() {    private lateinit var root: View    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        root = inflater.inflate(R.layout.fragment_chat, container, false)        // Inflate the layout for this fragment        return root    }    val TOPIC = "/topics/myTopic2"    private lateinit var viewModel: MainActivityViewModel    private val currentUserId = Constants.mAuth.currentUser!!.uid    private val adapter by lazy {        MessageAdapter()    }    private val RC_SELECT_IMAGE = 1    private val PICK_FROM_GALLERY = 11    var senderName = ""    private lateinit var user: User    private lateinit var group: GroupChannel    private lateinit var id: String    private lateinit var name: String    private lateinit var image: String    private lateinit var uid: String    private lateinit var nameRec: String    private lateinit var collectionName: String    private var isUser = true    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {        super.onViewCreated(view, savedInstanceState)        viewModel = (requireActivity() as MainActivity).viewModel        requireActivity().bottomNavigationView.visibility = View.GONE        (activity as AppCompatActivity).supportActionBar!!            .setDisplayHomeAsUpEnabled(true)        requireActivity().toolbarMain.setNavigationOnClickListener {            requireActivity().onBackPressed()        }        reChatMessage.setHasFixedSize(true)        reChatMessage.adapter = adapter        viewModel.profileUserLiveData.observe(requireActivity()            , androidx.lifecycle.Observer {                senderName = it.name!!            })        val bundel = requireArguments()        if (bundel.getString("type") == "group") {            isUser = false            group = bundel.getSerializable("group")!! as GroupChannel            name = group.group_name            image = group.photo            collectionName = Constants.COLLECTION_GROUPS            id = group.channelId            viewModel.getAllMessage(id, collectionName)            uid = group.channelId            nameRec = group.group_name            setHasOptionsMenu(true)        } else {            setHasOptionsMenu(false)            isUser = true            user = bundel.getSerializable("user")!! as User            getToken(user.token!!)            viewModel.getUser(user.uid!!, mapOf())            viewModel.userLiveData.observe(requireActivity(), androidx.lifecycle.Observer {                user = it                getTyping(user.isTyping)            })            name = user.name!!            image = user.profileImage!!            uid = user.uid!!            nameRec = user.name!!            viewModel.createChatChannel(user.uid!!) { id ->                viewModel.getAllMessage(id, Constants.COLLECTION_CHAT_CHANNEL)                this.id = id            }            collectionName = Constants.COLLECTION_CHAT_CHANNEL        }        requireActivity().titleToolbar.text = name        Glide.with(requireActivity())            .load(image)            .centerCrop()            .placeholder(R.drawable.ic_account_circle_black_24dp)            .into(requireActivity().btnImageProfile)        adapter.differ.currentList.clear()        viewModel.getMessage.observe(            requireActivity(),            androidx.lifecycle.Observer { list ->                adapter.differ.submitList(list)            })        btnChatSend.setOnClickListener {            val text = txtChatSend.text.toString()            if (text.trim().isEmpty()) {                return@setOnClickListener            }            val message = TextMessage(                text,                currentUserId,                uid,                Calendar.getInstance().time,                MessageType.TEXT,                senderName,                nameRec            )            txtChatSend.text.clear()            viewModel.getUser(uid, mapOf("isTyping" to 0))            sendChatChannel(message)        }        btnSendImage.setOnClickListener {            preimissionImger()        }        txtChatSend.addTextChangedListener(object : TextWatcher {            override fun afterTextChanged(s: Editable?) {            }            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {            }            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {                if (s!!.isEmpty()) {                    viewModel.getUser(uid, mapOf("isTyping" to 0))                } else {                    viewModel.getUser(uid, mapOf("isTyping" to 1))                }            }        })    }    private fun getTyping(typing: Int) {        if (typing == 1) {            txtLoading.visibility = View.VISIBLE            Log.e("tttt", "$typing")            return        } else {            txtLoading.visibility = View.GONE            Log.e("tttt", "$typing")        }    }    private fun getToken(tokenId: String) {        FirebaseService.sharedPref =            activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {            FirebaseService.token = tokenId        }    }    private fun sendChatChannel(message: TextMessage) {        viewModel.sentMessage(id, message, collectionName)        if (isUser) {            viewModel.updateUser(                user.uid!!,                mutableMapOf("lastMessage" to message.text, "date" to message.date)            )            viewModel.updateUser(                Constants.mAuth.uid!!,                mutableMapOf("lastMessage" to message.text, "date" to message.date)            )            if (user.statusUser == 0)                PushNotification(                    NotificationData(senderName, message.text),                    user.token!!                ).also {                    sendNotification(it)                }        } else {            for (i in group.arrayList) {                getToken(i)                PushNotification(                    NotificationData(senderName, message.text),                    i                ).also {                    sendNotification(it)                }            }        }    }    private fun sendNotification(notification: PushNotification) =        CoroutineScope(Dispatchers.IO).launch {            try {                RetrofitInstance                    .apiMessage                    .postNotification(notification)            } catch (e: Exception) {                e.printStackTrace()            }        }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)        if (requestCode == RC_SELECT_IMAGE &&            resultCode == Activity.RESULT_OK        ) {            val selectImageBmp = MediaStore                .Images.Media.getBitmap(                    requireActivity().contentResolver                    , data!!.data                )            val outputStream = ByteArrayOutputStream()            selectImageBmp                .compress(Bitmap.CompressFormat.JPEG, 20, outputStream)            val selectedImgByte = outputStream.toByteArray()            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {                viewModel.uploadImage(selectedImgByte) {                    val imageMessage = TextMessage(                        it,                        currentUserId,                        uid,                        Calendar.getInstance().time,                        MessageType.IMAGE,                        senderName,                        nameRec                    )                    viewModel.sentMessage(id, imageMessage, collectionName)                }            }        }    }    private fun addImage() {        val intent = Intent().apply {            type = "image/*"            action = Intent.ACTION_GET_CONTENT            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))        }        startActivityForResult(            Intent.createChooser(intent, "Select Image"),            RC_SELECT_IMAGE        )    }    private fun preimissionImger() {        try {            if (ActivityCompat.checkSelfPermission(                    requireActivity(),                    Manifest.permission.READ_EXTERNAL_STORAGE                ) != PackageManager.PERMISSION_GRANTED            ) {                ActivityCompat.requestPermissions(                    requireActivity(),                    arrayOf(                        Manifest.permission.READ_EXTERNAL_STORAGE,                        Manifest.permission.WRITE_EXTERNAL_STORAGE                    ),                    PICK_FROM_GALLERY                )            } else {                addImage()            }        } catch (e: Exception) {            e.printStackTrace()        }    }    override fun onRequestPermissionsResult(        requestCode: Int,        permissions: Array<String>,        grantResults: IntArray    ) {        when (requestCode) {            PICK_FROM_GALLERY ->                if (grantResults.isNotEmpty() &&                    grantResults[0] == PackageManager.PERMISSION_GRANTED                ) {                    addImage()                }        }    }    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {        requireActivity().menuInflater.inflate(R.menu.delete_group, menu)        super.onCreateOptionsMenu(menu, inflater)    }    override fun onOptionsItemSelected(item: MenuItem): Boolean {        when (item.itemId) {            R.id.deleteGroup -> {                viewModel.deleteGroup(group.channelId).addOnSuccessListener {                    viewModel.getGroupChannel()                    requireActivity().onBackPressed()                }            }        }        return super.onOptionsItemSelected(item)    }}