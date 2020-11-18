package com.exomatik.zcodex.ui.main.faq

import androidx.recyclerview.widget.LinearLayoutManager
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseFragmentBind
import com.exomatik.zcodex.databinding.FragmentFaqBinding
import com.exomatik.zcodex.model.ModelFaq

class FaqFragment : BaseFragmentBind<FragmentFaqBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_faq

    override fun myCodeHere() {
        bind.rcFaq.setHasFixedSize(true)
        bind.rcFaq.layoutManager = LinearLayoutManager(context)
        bind.rcFaq.adapter = FaqAdapter(faq())
    }

    private fun faq() : ArrayList<ModelFaq>{
        val listFaq = ArrayList<ModelFaq>()

        listFaq.add(ModelFaq("1. Apa itu aplikasi ZCode ?", "Aplikasi ZCode merupakan aplikasi pencatatan dengan berbagai fitur, salah satu fiturnya ialah mengumpulkan poin yang dapat diuangkan."))
        listFaq.add(ModelFaq("2. Apa itu Poin ?", "Poin merupakan sebuah nilai yang dapat ditukar dengan uang, harga 1 poin dapat berubah seiring waktu berjalan tergantung pada pengguna aplikasi ini, jadi semakin banyak pengguna semakin besar nilainya."))
        listFaq.add(ModelFaq("3. Bagaimana cara mendapatkan poin ?", "Anda mendapatkan poin dengan cara menekan tombol rewarded ads, kemudian menunggu sampai hitungan mundurnya selesai."))
        listFaq.add(ModelFaq("4. Bagaimana cara menukar poin dengan uang ?", "Poin dapat ditukar menjadi uang ketika mencapai nilai tertentu."))
        listFaq.add(ModelFaq("5. Ketentuan bersama itu apa ?", "Jadi karena kita menggunakan admbob sebagai periklanan kita maka dari itu kita mengikuti ketentuan yang admob berikan, yakni penarikan hanya bisa dilakukan ketika penghasilan iklan telah mencapai 1.3 jt kemudian baru bisa dicairkan tiap tanggal 21 per bulannya."))
        listFaq.add(ModelFaq("6. Apakah setiap pengguna harus mengumpulkan 1.3 juta untuk melakukan pencairan point ?", "Jadi untuk pencairan 1.3 juta maksudnya perusahaan(Developer Aplikasi) hanya bisa menarik dana dan membagikannya ke pengguna aplikasi ketika nilainya mencapai minimal 1.3 pada tanggal 21 tiap bulannya, Artinya kalau besok sudah 1.3 juta, sudah bisa dibagikan dengan syarat kepada para pengguna. Catatan : Tiap pengguna tidak harus mengumpulkan 1.3 juta."))
        return listFaq
    }
}