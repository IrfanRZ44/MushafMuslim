package com.exomatik.baseapplication.ui.main.beranda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.request.CachePolicy
import com.exomatik.baseapplication.R
import com.exomatik.baseapplication.model.ModelUser
import com.exomatik.baseapplication.utils.Constant.defaultTempFoto
import com.exomatik.baseapplication.utils.onClickFoto
import kotlinx.android.synthetic.main.item_kontak.view.*

class AdapterDataBeranda(
    private val listAfiliasi: ArrayList<ModelUser>,
    private val onClik: (ModelUser) -> Unit,
    private val navController: NavController
) : RecyclerView.Adapter<AdapterDataBeranda.AfiliasiHolder>() {

    inner class AfiliasiHolder(private val itemAfiliasi: View) :
        RecyclerView.ViewHolder(itemAfiliasi) {
        fun bindAfiliasi(
            itemData: ModelUser,
            onClik: (ModelUser) -> Unit) {

            itemAfiliasi.textNama.text = itemData.nama
            itemAfiliasi.imgFoto.load(defaultTempFoto) {
                crossfade(true)
                placeholder(R.drawable.ic_camera_white)
                error(R.drawable.ic_camera_white)
                fallback(R.drawable.ic_camera_white)
                memoryCachePolicy(CachePolicy.ENABLED)
            }

            itemAfiliasi.imgFoto.setOnClickListener {
                onClickFoto(defaultTempFoto,
                    navController)
            }

            itemAfiliasi.setOnClickListener {
                onClik(itemData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AfiliasiHolder {
        return AfiliasiHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_kontak,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listAfiliasi.size
    override fun onBindViewHolder(holder: AfiliasiHolder, position: Int) {
        holder.bindAfiliasi(listAfiliasi[position], onClik)
    }
}
