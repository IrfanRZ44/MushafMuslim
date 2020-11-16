package com.exomatik.zcodex.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.exomatik.zcodex.R

class SpinnerAdapter(private val context: Context?,
                     private val dataSpinner: ArrayList<String>) : BaseAdapter(),
    SpinnerAdapter {
    override fun getCount(): Int {
        return dataSpinner.size
    }
    override fun getItem(position: Int): Any {
        return dataSpinner[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.spinner_layout, null)
        val textView: TextView = view.findViewById(R.id.textNama)
        textView.text = dataSpinner[position]
        return textView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.spinner_layout, null)
        val textView: TextView = view.findViewById(R.id.textNama)
        textView.text = dataSpinner[position]

        return view
    }

}