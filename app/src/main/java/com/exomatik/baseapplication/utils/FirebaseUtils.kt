package com.exomatik.baseapplication.utils

//object FirebaseUtils {
//    private lateinit var database: FirebaseDatabase
//    private lateinit var query: Query
//    private lateinit var refreshData: ValueEventListener
//    private lateinit var query2: Query
//    private lateinit var refreshData2: ValueEventListener
//    private lateinit var query3: Query
//    private lateinit var refreshData3: ValueEventListener
//    private lateinit var queryDataUserChat: Query
//    private lateinit var refreshDataUserChat: ValueEventListener
//    private lateinit var queryDataChat: Query
//    private lateinit var refreshDataChat: ChildEventListener
//    private lateinit var queryListChat2: Query
//    private lateinit var refreshListChat2: ChildEventListener
//    private lateinit var queryListChat3: Query
//    private lateinit var refreshListChat3: ChildEventListener
//    private lateinit var queryListChat4: Query
//    private lateinit var refreshListChat4: ChildEventListener
//    private lateinit var queryListChat5: Query
//    private lateinit var refreshListChat5: ChildEventListener
//    private lateinit var queryListChat6: Query
//    private lateinit var refreshListChat6: ChildEventListener
//    private lateinit var queryIdChat: Query
//    private lateinit var refreshIdChat: ValueEventListener
//    private lateinit var queryIdChat2: Query
//    private lateinit var refreshIdChat2: ValueEventListener
//    private lateinit var queryNotifJadwal1: Query
//    private lateinit var refreshNotifJadwal1: ValueEventListener
//    private lateinit var queryNotifJadwal2: Query
//    private lateinit var refreshNotifJadwal2: ValueEventListener
//    private lateinit var queryAfiliasi: Query
//    private lateinit var refreshDataAfiliasi: ValueEventListener
//    private lateinit var queryAfiliasi2: Query
//    private lateinit var refreshDataAfiliasi2: ValueEventListener
//
//    fun saveJadwalWithUnique1Child(
//        reference: String, data: ModelJadwal
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        val ref = FirebaseDatabase.getInstance().getReference(reference)
//        val id = ref.push()
//        data.idJadwal = id.key.toString()
//        id.setValue(data)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun simpanNotif(data: ModelNotif) {
//        val ref = FirebaseDatabase.getInstance().getReference(referenceNotif)
//        val id = ref.push()
//        data.idNotif = id.key.toString()
//        database.getReference(referenceNotif)
//            .child(data.idNotif)
//            .setValue(data)
//    }
//
//    fun searchDataNotif(value: String?, eventListener: ValueEventListener) {
//        FirebaseDatabase.getInstance()
//            .getReference(referenceNotif)
//            .orderByChild("id")
//            .equalTo(value)
//            .addListenerForSingleValueEvent(eventListener)
//    }
//
//    fun sendNotif(sender: Sender) {
//        val mService: APIService = Common.fCMClient
//        mService.sendNotification(sender)
//            ?.enqueue(object : Callback<MyResponse?> {
//                override fun onResponse(
//                    call: Call<MyResponse?>?,
//                    response: Response<MyResponse?>?
//                ) {
//                    if (response != null) {
//                        if (response.isSuccessful) {
//                            showLog("Notify Succes")
//                        } else {
//                            showLog("Notify Failed")
//                        }
//                    } else {
//                        showLog("Null Response")
//                    }
//                }
//
//                override fun onFailure(call: Call<MyResponse?>?, t: Throwable) {
//                    showLog(t.message)
//                }
//            })
//    }
//
//    fun searchDataWith1ChildObject(
//        reference: String,
//        search: String,
//        value: String?,
//        eventListener: ValueEventListener
//    ) {
//        FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .orderByChild(search)
//            .equalTo(value)
//            .addListenerForSingleValueEvent(eventListener)
//    }
//
//    fun searchDataWith2ChildObject(
//        reference: String,
//        child: String,
//        search: String,
//        value: String?,
//        eventListener: ValueEventListener
//    ) {
//        FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child)
//            .orderByChild(search)
//            .equalTo(value)
//            .addListenerForSingleValueEvent(eventListener)
//    }
//
//    fun getDataObject(reference: String, eventListener: ValueEventListener) {
//        FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .addListenerForSingleValueEvent(eventListener)
//    }
//
//    fun getData1Child(reference: String, value: String, eventListener: ValueEventListener) {
//        FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(value)
//            .addListenerForSingleValueEvent(eventListener)
//    }
//
//    fun getData2Child(
//        reference: String,
//        value: String,
//        value2: String,
//        eventListener: ValueEventListener
//    ) {
//        FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(value)
//            .child(value2)
//            .addListenerForSingleValueEvent(eventListener)
//    }
//
////    fun searchWordWith2ChildObject(
////        reference: String,
////        child: String,
////        search: String,
////        value: String?,
////        eventListener: ValueEventListener
////    ) {
////        FirebaseDatabase.getInstance()
////            .getReference(reference)
////            .child(child)
////            .orderByChild(search)
////            .startAt(value)
////            .endAt(value + "\uf8ff")
////            .addListenerForSingleValueEvent(eventListener)
////    }
////
////    fun searchWordWith1ChildObject(
////        reference: String,
////        search: String,
////        value: String?,
////        eventListener: ValueEventListener
////    ) {
////        FirebaseDatabase.getInstance()
////            .getReference(reference)
////            .orderByChild(search)
////            .startAt(value)
////            .endAt(value + "\uf8ff")
////            .addListenerForSingleValueEvent(eventListener)
////    }
//
//    fun refreshDataWith1ChildObject1(
//        reference: String,
//        id: String,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshData = eventListener
//        query = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(id)
//        query.addValueEventListener(refreshData)
//    }
//
//    fun refreshDataWith2ChildObject1(
//        reference: String,
//        child: String,
//        id: String,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshData2 = eventListener
//        query2 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child)
//            .child(id)
//        query2.addValueEventListener(refreshData2)
//    }
//
//    fun refreshDataWith3ChildObject1(
//        reference: String,
//        child: String,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshData3 = eventListener
//        query3 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child)
//        query3.addValueEventListener(refreshData3)
//    }
//
//    fun refreshDataUserChatWith2Child(
//        reference: String,
//        search: String,
//        value: String?,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshDataUserChat = eventListener
//
//        queryDataUserChat = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .orderByChild(search)
//            .equalTo(value)
//        queryDataUserChat.addValueEventListener(refreshDataUserChat)
//    }
//
//    fun refreshDataChatWith2Child(
//        reference: String,
//        child1: String,
//        idChat: String,
//        eventListener: ChildEventListener
//    ) {
//        this.refreshDataChat = eventListener
//
//        queryDataChat = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child1)
//            .child(idChat)
//        queryDataChat.addChildEventListener(refreshDataChat)
//    }
//
//    fun refresh2ListChatWith1Child(
//        reference: String,
//        child1: String,
//        search: String,
//        value: String?,
//        eventListener: ChildEventListener
//    ) {
//        this.refreshListChat2 = eventListener
//
//        queryListChat2 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child1)
//            .orderByChild(search)
//            .equalTo(value)
//        queryListChat2.addChildEventListener(refreshListChat2)
//    }
//
//    fun refresh3ListChatWith1Child(
//        reference: String,
//        child1: String,
//        search: String,
//        value: String?,
//        eventListener: ChildEventListener
//    ) {
//        this.refreshListChat3 = eventListener
//
//        queryListChat3 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child1)
//            .orderByChild(search)
//            .equalTo(value)
//        queryListChat3.addChildEventListener(refreshListChat3)
//    }
//
//    fun refresh4ListChatWith1Child(
//        reference: String,
//        child1: String,
//        search: String,
//        value: String?,
//        eventListener: ChildEventListener
//    ) {
//        this.refreshListChat4 = eventListener
//
//        queryListChat4 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child1)
//            .orderByChild(search)
//            .equalTo(value)
//        queryListChat4.addChildEventListener(refreshListChat4)
//    }
//
//    fun refresh5ListChatWith1Child(
//        reference: String,
//        child1: String,
//        search: String,
//        value: String?,
//        eventListener: ChildEventListener
//    ) {
//        this.refreshListChat5 = eventListener
//
//        queryListChat5 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child1)
//            .orderByChild(search)
//            .equalTo(value)
//        queryListChat5.addChildEventListener(refreshListChat5)
//    }
//
//    fun refresh6ListChatWith1Child(
//        reference: String,
//        child1: String,
//        search: String,
//        value: String?,
//        eventListener: ChildEventListener
//    ) {
//        this.refreshListChat6 = eventListener
//
//        queryListChat6 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child1)
//            .orderByChild(search)
//            .equalTo(value)
//        queryListChat6.addChildEventListener(refreshListChat6)
//    }
//
//    fun refreshGetIdChatWith2Child(
//        reference: String,
//        child1: String,
//        idChat: String,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshIdChat = eventListener
//
//        queryIdChat = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child1)
//            .child(idChat)
//        queryIdChat.addValueEventListener(refreshIdChat)
//    }
//
//    fun refreshNotifJadwalWith2Child(
//        reference: String,
//        search: String,
//        value: String,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshNotifJadwal1 = eventListener
//
//        queryNotifJadwal1 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .orderByChild(search)
//            .equalTo(value)
//        queryNotifJadwal1.addValueEventListener(refreshNotifJadwal1)
//    }
//
//    fun refreshNotif2JadwalWith2Child(
//        reference: String,
//        search: String,
//        value: String?,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshNotifJadwal2 = eventListener
//
//        queryNotifJadwal2 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .orderByChild(search)
//            .equalTo(value)
//        queryNotifJadwal2.addValueEventListener(refreshNotifJadwal2)
//    }
//
//    fun refreshAfiliasi1DataWith2ChildObject(
//        reference: String,
//        child: String,
//        search: String,
//        value: String?,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshDataAfiliasi = eventListener
//        queryAfiliasi = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child)
//            .orderByChild(search)
//            .equalTo(value)
//        queryAfiliasi.addValueEventListener(eventListener)
//    }
//
//    fun refreshAfiliasi2DataWith2ChildObject(
//        reference: String,
//        child: String,
//        value: String,
//        eventListener: ValueEventListener
//    ) {
//        this.refreshDataAfiliasi2 = eventListener
//        queryAfiliasi2 = FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .child(child)
//            .child(value)
//        queryAfiliasi2.addValueEventListener(eventListener)
//    }
//
//    fun registerUser(
//        phone: String
//        , phoneAuthProvider: PhoneAuthProvider.OnVerificationStateChangedCallbacks
//        , activity: Activity
//    ) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//            phone
//            , 60
//            , TimeUnit.SECONDS
//            , activity
//            , phoneAuthProvider
//        )
//    }
//
//    fun getUserToken(onCompleteListener: OnCompleteListener<InstanceIdResult>) {
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(onCompleteListener)
//    }
//
//    fun signIn(credential: AuthCredential, onCompleteListener: OnCompleteListener<AuthResult>) {
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//            .addOnCompleteListener(onCompleteListener)
//    }
//
//    fun signOut() {
//        FirebaseAuth.getInstance().signOut()
//    }
//
//    fun setValueObject(
//        reference: String, child: String, data: Any
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .setValue(data)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith0ChildObject(
//        reference: String, data: Any
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .setValue(data)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith1ChildObject(
//        reference: String, child: String, data: Any
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .setValue(data)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith2ChildObject(
//        reference: String, child: String, child2: String, data: Any
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .setValue(data)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith3ChildObject(
//        reference: String, child: String, child2: String, child3: String, data: Any
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .child(child3)
//            .setValue(data)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith2ChildString(
//        reference: String, child: String, child2: String, value: String
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .setValue(value)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith3ChildBoolean(
//        reference: String, child: String, child2: String, child3: String, value: Boolean
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .child(child3)
//            .setValue(value)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith3ChildString(
//        reference: String, child: String, child2: String, child3: String, value: String
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .child(child3)
//            .setValue(value)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun setValueWith4ChildString(
//        reference: String,
//        child: String,
//        child2: String,
//        child3: String,
//        child4: String,
//        value: String
//        ,
//        onCompleteListener: OnCompleteListener<Void>
//        ,
//        onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .child(child3)
//            .child(child4)
//            .setValue(value)
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun deleteValueWith3Child(
//        reference: String, child: String, child2: String, child3: String
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .child(child3)
//            .removeValue()
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun deleteValueWith2Child(
//        reference: String, child: String, child2: String
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .child(child2)
//            .removeValue()
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun deleteValueWith1Child(
//        reference: String, child: String
//        , onCompleteListener: OnCompleteListener<Void>
//        , onFailureListener: OnFailureListener
//    ) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(reference)
//            .child(child)
//            .removeValue()
//            .addOnCompleteListener(onCompleteListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun deleteNotif(idNotif: String) {
//        database = FirebaseDatabase.getInstance()
//        database.getReference(referenceNotif)
//            .child(idNotif)
//            .removeValue()
//    }
//
//    fun deleteUrlFoto(url: String) {
//        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
//        storageRef.delete()
//    }
//
//    fun simpanFoto(
//        reference: String, id: String, image: Uri
//        , onSuccessListener: OnSuccessListener<UploadTask.TaskSnapshot>
//        , onFailureListener: OnFailureListener
//    ) {
//        val mStorageRef = FirebaseStorage.getInstance().reference
//        mStorageRef.child("$reference/$id").putFile(image)
//            .addOnSuccessListener(onSuccessListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun simpanFoto3Child(
//        reference: String, child: String, child2: String, id: String, image: Uri
//        , onSuccessListener: OnSuccessListener<UploadTask.TaskSnapshot>
//        , onFailureListener: OnFailureListener
//    ) {
//        val mStorageRef = FirebaseStorage.getInstance().reference
//        mStorageRef.child("$reference/$child/$child2/$id").putFile(image)
//            .addOnSuccessListener(onSuccessListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun getUrlFoto(
//        uploadTask: UploadTask.TaskSnapshot
//        , onSuccessListener: OnSuccessListener<Uri?>
//        , onFailureListener: OnFailureListener
//    ) {
//        uploadTask.storage.downloadUrl
//            .addOnSuccessListener(onSuccessListener)
//            .addOnFailureListener(onFailureListener)
//    }
//
//    fun stopRefresh() {
//        try {
//            query.removeEventListener(refreshData)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query 1")
//        }
//    }
//
//    fun stopRefresh2() {
//        try {
//            query2.removeEventListener(refreshData2)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query 2")
//        }
//    }
//
//    fun stopRefresh3() {
//        try {
//            query3.removeEventListener(refreshData3)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query 3")
//        }
//    }
//
//    fun stopRefreshNotifJadwal1() {
//        try {
//            queryNotifJadwal1.removeEventListener(refreshNotifJadwal1)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query notif jadwal 1")
//        }
//    }
//
//    fun stopRefreshNotifJadwal2() {
//        try {
//            queryNotifJadwal2.removeEventListener(refreshNotifJadwal2)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query notif jadwal 2")
//        }
//    }
//
//    fun stopRefreshDataUserChat() {
//        try {
//            queryDataUserChat.removeEventListener(refreshDataUserChat)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query data user chat")
//        }
//    }
//
//    fun stopRefreshDataChat() {
//        try {
//            queryDataChat.removeEventListener(refreshDataChat)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query data chat")
//        }
//    }
//
//    fun stopRefreshListChat2() {
//        try {
//            queryListChat2.removeEventListener(refreshListChat2)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query data chat 2")
//        }
//    }
//
//    fun stopRefreshListChat3() {
//        try {
//            queryListChat3.removeEventListener(refreshListChat3)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query data chat 3")
//        }
//    }
//
//    fun stopRefreshListChat4() {
//        try {
//            queryListChat4.removeEventListener(refreshListChat4)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query data chat 4")
//        }
//    }
//
//    fun stopRefreshListChat5() {
//        try {
//            queryListChat5.removeEventListener(refreshListChat5)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query data chat 5")
//        }
//    }
//
//    fun stopRefreshListChat6() {
//        try {
//            queryListChat6.removeEventListener(refreshListChat6)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query data chat 6")
//        }
//    }
//
//    fun stopRefreshIdChat() {
//        try {
//            queryIdChat.removeEventListener(refreshIdChat)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query id chat")
//        }
//    }
//
//    fun stopRefreshAfiliasi1() {
//        try {
//            queryAfiliasi.removeEventListener(refreshDataAfiliasi)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query afiliasi 1")
//        }
//    }
//
//    fun stopRefreshAfiliasi2() {
//        try {
//            queryAfiliasi2.removeEventListener(refreshDataAfiliasi2)
//        } catch (e: Exception) {
//            showLog("error, method not running ${e.message} query afiliasi 2")
//        }
//    }
//}