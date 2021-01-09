package id.exomatik.mushafmuslim.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import id.exomatik.mushafmuslim.utils.DataSave

abstract class BaseActivityBind<B : ViewDataBinding>  : AppCompatActivity(){
    protected lateinit var bind: B
    protected abstract fun myCodeHere()
    protected abstract fun getLayoutResource(): Int
    protected lateinit var savedData : DataSave

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = DataBindingUtil.setContentView(this, getLayoutResource())
        savedData = DataSave(this)
        myCodeHere()
    }
}