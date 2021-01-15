package id.exomatik.mushafmuslim.utils

import android.app.Activity
import android.net.Uri
import id.exomatik.mushafmuslim.model.ModelNotes
import id.exomatik.mushafmuslim.model.ModelTransaction
import id.exomatik.mushafmuslim.services.notification.APIService
import id.exomatik.mushafmuslim.services.notification.Common
import id.exomatik.mushafmuslim.services.notification.model.MyResponse
import id.exomatik.mushafmuslim.services.notification.model.Sender
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import id.exomatik.mushafmuslim.model.ModelPenarikan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

object FirebaseUtils {
    private lateinit var database: FirebaseDatabase
    private lateinit var query: Query
    private lateinit var refreshData: ValueEventListener
    private lateinit var query2: Query
    private lateinit var refreshData2: ValueEventListener

    fun sendNotif(sender: Sender) {
        val mService: APIService = Common.fCMClient
        mService.sendNotification(sender)?.enqueue(object : Callback<MyResponse?> {
            override fun onResponse(
                call: Call<MyResponse?>?,
                response: Response<MyResponse?>?
            ) {
                if (response != null) {
                    if (response.isSuccessful) {
                        showLog("Notify Succes")
                    } else {
                        showLog("Notify Failed")
                    }
                } else {
                    showLog("Null Response")
                }
            }

            override fun onFailure(call: Call<MyResponse?>?, t: Throwable) {
                showLog(t.message)
            }
        })
    }

    fun searchDataWith1ChildObject(
        reference: String,
        search: String,
        value: String?,
        eventListener: ValueEventListener
    ) {
        FirebaseDatabase.getInstance()
            .getReference(reference)
            .orderByChild(search)
            .equalTo(value)
            .addListenerForSingleValueEvent(eventListener)
    }

    fun setValueUniqueTransaction(reference: String, child: String, data: ModelTransaction,
                                  onCompleteListener: OnCompleteListener<Void>
                                  , onFailureListener: OnFailureListener) {
        val ref = FirebaseDatabase.getInstance().getReference(reference)
        val id = ref.push()
        data.idTransaction = id.key.toString()

        FirebaseDatabase.getInstance().getReference(reference)
            .child(child)
            .child(data.idTransaction)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueUniquePenarikan(reference: String, data: ModelPenarikan,
                                  onCompleteListener: OnCompleteListener<Void>
                                  , onFailureListener: OnFailureListener) {
        val ref = FirebaseDatabase.getInstance().getReference(reference)
        val id = ref.push()
        data.idPenarikan = id.key.toString()

        FirebaseDatabase.getInstance().getReference(reference)
            .child(data.idPenarikan)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueUniqueNotes(reference: String, child: String, data: ModelNotes,
                            onCompleteListener: OnCompleteListener<Void>
                            , onFailureListener: OnFailureListener) {
        val ref = FirebaseDatabase.getInstance().getReference(reference)
        val id = ref.push()
        data.idNotes = id.key.toString()

        FirebaseDatabase.getInstance().getReference(reference)
            .child(data.username)
            .child(data.idNotes)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun searchDataWith2ChildObject(
        reference: String,
        child: String,
        search: String,
        value: String?,
        eventListener: ValueEventListener
    ) {
        FirebaseDatabase.getInstance()
            .getReference(reference)
            .child(child)
            .orderByChild(search)
            .equalTo(value)
            .addListenerForSingleValueEvent(eventListener)
    }

    fun getDataObject(reference: String, eventListener: ValueEventListener) {
        FirebaseDatabase.getInstance()
            .getReference(reference)
            .addListenerForSingleValueEvent(eventListener)
    }

    fun getData1Child(reference: String, value: String, eventListener: ValueEventListener) {
        FirebaseDatabase.getInstance()
            .getReference(reference)
            .child(value)
            .addListenerForSingleValueEvent(eventListener)
    }

    fun getData2Child(
        reference: String,
        value: String,
        value2: String,
        eventListener: ValueEventListener
    ) {
        FirebaseDatabase.getInstance()
            .getReference(reference)
            .child(value)
            .child(value2)
            .addListenerForSingleValueEvent(eventListener)
    }

//    fun searchWordWith2ChildObject(
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
//            .startAt(value)
//            .endAt(value + "\uf8ff")
//            .addListenerForSingleValueEvent(eventListener)
//    }
//
//    fun searchWordWith1ChildObject(
//        reference: String,
//        search: String,
//        value: String?,
//        eventListener: ValueEventListener
//    ) {
//        FirebaseDatabase.getInstance()
//            .getReference(reference)
//            .orderByChild(search)
//            .startAt(value)
//            .endAt(value + "\uf8ff")
//            .addListenerForSingleValueEvent(eventListener)
//    }

    fun refreshDataWith1ChildObject1(
        reference: String,
        id: String,
        eventListener: ValueEventListener
    ) {
        this.refreshData = eventListener
        query = FirebaseDatabase.getInstance()
            .getReference(reference)
            .child(id)
        query.addValueEventListener(refreshData)
    }

    fun refreshDataWith1ChildObject2(
        reference: String,
        id: String,
        eventListener: ValueEventListener
    ) {
        this.refreshData2 = eventListener
        query2 = FirebaseDatabase.getInstance()
            .getReference(reference)
            .child(id)
        query2.addValueEventListener(refreshData2)
    }

    fun registerUser(
        phone: String
        , phoneAuthProvider: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        , activity: Activity
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone
            , 60
            , TimeUnit.SECONDS
            , activity
            , phoneAuthProvider
        )
    }

    fun getUserToken(onCompleteListener: OnCompleteListener<InstanceIdResult>) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(onCompleteListener)
    }

    fun signIn(credential: AuthCredential, onCompleteListener: OnCompleteListener<AuthResult>) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(onCompleteListener)
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun setValueObject(
        reference: String, child: String, data: Any
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith0ChildObject(
        reference: String, data: Any
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith1ChildObject(
        reference: String, child: String, data: Any
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith2ChildObject(
        reference: String, child: String, child2: String, data: Any
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith3ChildObject(
        reference: String, child: String, child2: String, child3: String, data: Any
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .child(child3)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith3ChildInt(
        reference: String, child: String, child2: String, data: Long
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .setValue(data)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith2ChildString(
        reference: String, child: String, child2: String, value: String
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .setValue(value)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith2ChildLong(
        reference: String, child: String, child2: String, value: Long
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .setValue(value)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith3ChildBoolean(
        reference: String, child: String, child2: String, child3: String, value: Boolean
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .child(child3)
            .setValue(value)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith3ChildString(
        reference: String, child: String, child2: String, child3: String, value: String
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .child(child3)
            .setValue(value)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun setValueWith4ChildString(
        reference: String,
        child: String,
        child2: String,
        child3: String,
        child4: String,
        value: String
        ,
        onCompleteListener: OnCompleteListener<Void>
        ,
        onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .child(child3)
            .child(child4)
            .setValue(value)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun deleteValueWith3Child(
        reference: String, child: String, child2: String, child3: String
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .child(child3)
            .removeValue()
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun deleteValueWith2Child(
        reference: String, child: String, child2: String
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .child(child2)
            .removeValue()
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun simpanFoto(
        reference: String, id: String, image: Uri
        , onSuccessListener: OnSuccessListener<UploadTask.TaskSnapshot>
        , onFailureListener: OnFailureListener
    ) {
        FirebaseStorage.getInstance().getReference(reference)
            .child(id).putFile(image)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun getUrlFoto(
        uploadTask: UploadTask.TaskSnapshot
        , onSuccessListener: OnSuccessListener<Uri?>
        , onFailureListener: OnFailureListener
    ) {
        uploadTask.storage.downloadUrl
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun deleteValueWith1Child(
        reference: String, child: String
        , onCompleteListener: OnCompleteListener<Void>
        , onFailureListener: OnFailureListener
    ) {
        database = FirebaseDatabase.getInstance()
        database.getReference(reference)
            .child(child)
            .removeValue()
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    fun stopRefresh() : Boolean {
        return try {
            query.removeEventListener(refreshData)
            true
        } catch (e: Exception) {
            showLog("error, method not running ${e.message} query 1")
            false
        }
    }

    fun stopRefresh2() {
        try {
            query2.removeEventListener(refreshData2)
        } catch (e: Exception) {
            showLog("error, method not running ${e.message} query 2")
        }
    }
}