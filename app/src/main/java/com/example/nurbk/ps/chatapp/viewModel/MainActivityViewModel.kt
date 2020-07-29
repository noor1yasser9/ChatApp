package com.example.nurbk.ps.chatapp.viewModelimport android.app.Applicationimport android.icu.text.SimpleDateFormatimport android.os.Buildimport android.util.Logimport androidx.annotation.RequiresApiimport androidx.lifecycle.AndroidViewModelimport androidx.lifecycle.MutableLiveDataimport com.example.nurbk.ps.chatapp.model.GroupChannelimport com.example.nurbk.ps.chatapp.model.TextMessageimport com.example.nurbk.ps.chatapp.model.Userimport com.example.nurbk.ps.chatapp.repository.MainActivityRepositoryimport com.example.nurbk.ps.chatapp.unit.Constantsimport com.google.firebase.firestore.Queryimport java.util.*import kotlin.collections.ArrayListclass MainActivityViewModel(application: Application) : AndroidViewModel(application) {    private val TAG = "MainActivityViewModel"    private val repository = MainActivityRepository()    val profileUserLiveData = MutableLiveData<User>()    val getAllUserLiveData = MutableLiveData<List<User>>()    val getMessage = MutableLiveData<List<TextMessage>>()    val getChatGroupChannel = MutableLiveData<List<GroupChannel>>()    init {        repository.getProfileData {            profileUserLiveData.value = it        }        getAllUser()        getGroupChannel()    }    fun getAllUser() {        repository.getAllUsers()            .orderBy("date", Query.Direction.DESCENDING)            .get().addOnCompleteListener {                getAllUserLiveData.value =                    it.result!!.toObjects(User::class.java)            }    }    fun updateUser(uid: String, map: MutableMap<String, Any>) {        Constants.nameCollection(Constants.COLLECTION_USERS)            .document(uid)            .update(map)            .addOnSuccessListener {                getAllUser()            }    }    fun sentMessage(channelId: String, message: TextMessage, collectionName: String) =        repository.sentMessage(channelId, message, collectionName) {            getAllMessage(channelId, collectionName)        }    @RequiresApi(Build.VERSION_CODES.N)    private fun getTime(): String {        val time = SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.ms")        return time.format(Date())    }    @RequiresApi(Build.VERSION_CODES.N)    fun uploadImage(        selectedImageBytes: ByteArray,        onSuccess: (imagePath: String) -> Unit    ) {        val ref = Constants.storageInstance.reference            .child(Constants.mAuth.uid!!)            .child(getTime())        ref.putBytes(selectedImageBytes)            .addOnSuccessListener {                ref.downloadUrl.addOnSuccessListener {                    onSuccess(it.toString())                }            }.addOnFailureListener {                Log.e(TAG, "uploadImage->addOnFailureListener")            }    }    fun createChatChannel(        uid: String,        onComplete: (channelId: String) -> Unit    ) =        repository.createChatChannel(uid, onComplete)    fun getAllMessage(channelId: String, collectionName: String) =        repository.getMessage(channelId, collectionName)            .get().addOnCompleteListener {                getMessage.value = it.result!!.toObjects(TextMessage::class.java)            }    fun   getGroupChannel() {        Constants.nameCollection(Constants.COLLECTION_USERS)            .document(Constants.mAuth.uid!!)            .collection(Constants.COLLECTION_GROUP_CHANNEL)            .get().addOnCompleteListener {                getChatGroupChannel.value = it.result!!.toObjects(GroupChannel::class.java)            }    }    val userLiveData = MutableLiveData<User>()    fun getUser(uid: String, map: Map<String, Any>) {        Constants.nameCollection(Constants.COLLECTION_USERS)            .document(uid)            .get().addOnCompleteListener {                userLiveData.value = it.result!!.toObject(User::class.java)            }//        Constants.nameCollection(Constants.COLLECTION_USERS)//            .document(uid)//            .update(map)    }    fun deleteGroup(chatChannelId: String) =        Constants.nameCollection(Constants.COLLECTION_USERS)            .document(Constants.mAuth.uid!!)            .collection(Constants.COLLECTION_GROUP_CHANNEL)            .document(chatChannelId)            .delete()}