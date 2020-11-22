package com.exomatik.zcodex.ui.main.faq

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentFaqBinding

class FaqFragment : BaseFragmentBind<FragmentFaqBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_faq

    override fun myCodeHere() {
        val listFaq = savedData.getDataApps()?.data_faq

        if (listFaq != null){
            bind.textStatus.visibility = View.GONE
            bind.rcFaq.visibility = View.VISIBLE
            bind.rcFaq.setHasFixedSize(true)
            bind.rcFaq.layoutManager = LinearLayoutManager(context)
            bind.rcFaq.adapter = FaqAdapter(listFaq)
        }
        else{
            bind.textStatus.visibility = View.VISIBLE
            bind.rcFaq.visibility = View.GONE
        }
    }
}