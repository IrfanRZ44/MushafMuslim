package id.exomatik.bacashirah.ui.main.shirah

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.request.CachePolicy
import id.exomatik.bacashirah.R
import id.exomatik.bacashirah.model.ModelShirah
import id.exomatik.bacashirah.utils.onClickFoto
import kotlinx.android.synthetic.main.item_shirah.view.*

class AdapterShirah (private val listKelas : ArrayList<ModelShirah>,
                     private val onClick : (ModelShirah) -> Unit,
                     private val navController: NavController
) : RecyclerView.Adapter<AdapterShirah.AfiliasiHolder>(){

    inner class AfiliasiHolder(private val viewItem : View) : RecyclerView.ViewHolder(viewItem){
        @SuppressLint("SetTextI18n")
        fun bindAfiliasi(item: ModelShirah){
            viewItem.textJudul.text = item.title
            viewItem.textShirah.text = item.shirah
            viewItem.textTanggal.text = item.dateCreated
            viewItem.imgFoto.load(item.urlFoto) {
                crossfade(true)
                placeholder(R.drawable.ic_image_gray)
                error(R.drawable.ic_image_gray)
                fallback(R.drawable.ic_image_gray)
                memoryCachePolicy(CachePolicy.ENABLED)
            }

            viewItem.imgFoto.setOnClickListener {
                onClickFoto(item.urlFoto, navController)
            }

            viewItem.setOnClickListener {
                onClick(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AfiliasiHolder {
        return AfiliasiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_shirah, parent, false))
    }
    override fun getItemCount(): Int = listKelas.size
    override fun onBindViewHolder(holder: AfiliasiHolder, position: Int) {
        holder.bindAfiliasi(listKelas[position])
    }
}
