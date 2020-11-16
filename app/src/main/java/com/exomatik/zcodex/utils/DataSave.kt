package com.exomatik.zcodex.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.utils.Constant.referenceUser
import com.google.gson.Gson

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

    @SuppressLint("CommitPrefEdits")
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

    fun getKeyString(key: String): String? {
        return try {
            preferences?.getString(key, "") ?: throw Exception("Data Kosong")
        }catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }
}