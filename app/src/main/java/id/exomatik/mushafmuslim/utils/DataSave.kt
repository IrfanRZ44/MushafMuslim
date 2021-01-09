package id.exomatik.mushafmuslim.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import id.exomatik.mushafmuslim.model.ModelInfoApps
import id.exomatik.mushafmuslim.model.ModelUser
import id.exomatik.mushafmuslim.utils.Constant.referenceInfoApps
import id.exomatik.mushafmuslim.utils.Constant.referenceUser
import com.google.gson.Gson
import id.exomatik.mushafmuslim.model.ModelDataAccount
import id.exomatik.mushafmuslim.utils.Constant.referenceDataAccount

class DataSave(private val context: Context?) {
    private val preferences: SharedPreferences? = context?.getSharedPreferences("UserPref", 0)

    fun setDataObject(any: Any?, key: String) {
        try {
            val prefsEditor: SharedPreferences.Editor = preferences?.edit()
                ?: throw Exception("Preferences Belum Di Inisialisasikan")
            val gson = Gson()
            val json: String = gson.toJson(any)
            prefsEditor.putString(key, json)
            prefsEditor.apply()
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun getDataUser(): ModelUser? {
        return try {
            val gson = Gson()
            val json: String = preferences?.getString(referenceUser, "")
                ?: throw Exception("Preferences Belum Di Inisialisasikan")
            gson.fromJson(json, ModelUser::class.java)
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    fun getDataAccount(): ModelDataAccount? {
        return try {
            val gson = Gson()
            val json: String = preferences?.getString(referenceDataAccount, "")
                ?: throw Exception("Preferences Belum Di Inisialisasikan")
            gson.fromJson(json, ModelDataAccount::class.java)
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    fun getDataApps(): ModelInfoApps? {
        return try {
            val gson = Gson()
            val json: String = preferences?.getString(referenceInfoApps, "")
                ?: throw Exception("Preferences Belum Di Inisialisasikan")
            gson.fromJson(json, ModelInfoApps::class.java)
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    fun setDataString(value: String?, key: String) {
        try {
            val prefsEditor: SharedPreferences.Editor =
                preferences?.edit() ?: throw Exception("Preferences Belum Di Inisialisasikan")
            prefsEditor.putString(key, value)
            prefsEditor.apply()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun setDataInt(value: Int?, key: String) {
        try {
            val prefsEditor: SharedPreferences.Editor =
                preferences?.edit() ?: throw Exception("Preferences Belum Di Inisialisasikan")
            prefsEditor.putInt(key, value?:0)
            prefsEditor.apply()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun setDataBoolean(value: Boolean, key: String) {
        try {
            val prefsEditor: SharedPreferences.Editor =
                preferences?.edit() ?: throw Exception("Preferences Belum Di Inisialisasikan")
            prefsEditor.putBoolean(key, value)
            prefsEditor.apply()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun setDataLong(value: Long, key: String) {
        try {
            val prefsEditor: SharedPreferences.Editor =
                preferences?.edit() ?: throw Exception("Preferences Belum Di Inisialisasikan")
            prefsEditor.putLong(key, value)
            prefsEditor.apply()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun getKeyString(key: String): String? {
        return try {
            preferences?.getString(key, "") ?: throw Exception("Data Kosong")
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    fun getKeyLong(key: String): Long? {
        return try {
            preferences?.getLong(key, 0) ?: 0
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    fun getKeyBoolean(key: String): Boolean {
        return preferences?.getBoolean(key, false)?:false
    }

    fun getKeyInt(key: String): Int? {
        return try {
            preferences?.getInt(key, 0) ?: 0
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }
}