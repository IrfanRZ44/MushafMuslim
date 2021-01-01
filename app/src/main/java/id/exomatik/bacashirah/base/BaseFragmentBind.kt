package id.exomatik.bacashirah.base

import android.view.View
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import id.exomatik.bacashirah.utils.DataSave

abstract class BaseFragmentBind<B : ViewDataBinding> : Fragment() {
    protected lateinit var bind: B
    protected abstract fun getLayoutResource(): Int
    protected abstract fun myCodeHere()
    protected lateinit var savedData: DataSave
    protected var savedInstanceState: Bundle? = null
    protected var supportActionBar : ActionBar? = null

    override fun onCreateView(paramLayoutInflater: LayoutInflater, paramViewGroup: ViewGroup?, paramBundle: Bundle?): View? {
        supportActionBar = (activity as AppCompatActivity).supportActionBar
        bind = DataBindingUtil.inflate(layoutInflater, getLayoutResource(), paramViewGroup, false)

        savedInstanceState = paramBundle
        savedData = DataSave(context)
        myCodeHere()

        return bind.root
    }

}