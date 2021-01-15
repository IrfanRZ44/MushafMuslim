package id.exomatik.mushafmuslim.ui.main.riwayatPenarikan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.model.ModelPenarikan
import id.exomatik.mushafmuslim.utils.Constant
import kotlinx.android.synthetic.main.item_penarikan.view.*

class AdapterRiwayat (private val listKelas : ArrayList<ModelPenarikan>,
                      private val ctx: Context?) : RecyclerView.Adapter<AdapterRiwayat.AfiliasiHolder>(){

    inner class AfiliasiHolder(private val viewItem : View) : RecyclerView.ViewHolder(viewItem){
        @Suppress("DEPRECATION")
        fun bindAfiliasi(item: ModelPenarikan){
            viewItem.textJumlah.text = item.jumlah
            viewItem.textTanggal.text = item.tanggal
            viewItem.textStatus.text = item.status

            if (item.status == Constant.wdProses){
                ctx?.resources?.getColor(R.color.blue1)?.let {
                    viewItem.textStatus.setTextColor(
                        it
                    )
                }
            }
            else if (item.status == Constant.wdDitolak){
                ctx?.resources?.getColor(R.color.red1)?.let {
                    viewItem.textStatus.setTextColor(
                        it
                    )
                }
            }
            else if (item.status == Constant.wdBerhasil){
                ctx?.resources?.getColor(R.color.colorPrimary)?.let {
                    viewItem.textStatus.setTextColor(
                        it
                    )
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AfiliasiHolder {
        return AfiliasiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_penarikan, parent, false))
    }
    override fun getItemCount(): Int = listKelas.size
    override fun onBindViewHolder(holder: AfiliasiHolder, position: Int) {
        holder.bindAfiliasi(listKelas[position])
    }
}
