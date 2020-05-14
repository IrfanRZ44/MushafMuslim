package com.exomatik.baseapplication.base

import android.view.View
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.exomatik.baseapplication.utils.DataSave

abstract class BaseFragment : Fragment() {
    protected lateinit var v : View
    protected abstract fun getLayoutResource(): Int
    protected abstract fun myCodeHere()
    protected lateinit var savedData : DataSave

    override fun onCreateView(paramLayoutInflater: LayoutInflater, paramViewGroup: ViewGroup?, paramBundle: Bundle?): View? {
        v = paramLayoutInflater.inflate(getLayoutResource(), paramViewGroup, false)

        savedData = DataSave(context)
        myCodeHere()
        return v
    }
}