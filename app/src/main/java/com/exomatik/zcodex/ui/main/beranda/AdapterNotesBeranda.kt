package com.exomatik.zcodex.ui.main.beranda

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.R
import com.exomatik.zcodex.model.ModelNotes
import kotlinx.android.synthetic.main.item_notes.view.*

class AdapterNotesBeranda (private val listKelas : ArrayList<ModelNotes>,
                           private val onClickItem: (ModelNotes) -> Unit
) : RecyclerView.Adapter<AdapterNotesBeranda.AfiliasiHolder>(){

    inner class AfiliasiHolder(private val viewItem : View) : RecyclerView.ViewHolder(viewItem){

        @SuppressLint("SetTextI18n")
        fun bindAfiliasi(item: ModelNotes){
            viewItem.textName.text = item.title
            viewItem.textNotes.text = item.notes
            viewItem.textTanggal.text = "Last edited ${item.tglDiedit}"

            viewItem.setOnClickListener {
                onClickItem(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AfiliasiHolder {
        return AfiliasiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false))
    }
    override fun getItemCount(): Int = listKelas.size
    override fun onBindViewHolder(holder: AfiliasiHolder, position: Int) {
        holder.bindAfiliasi(listKelas[position])
    }
}
