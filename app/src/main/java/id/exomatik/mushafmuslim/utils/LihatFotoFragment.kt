package id.exomatik.mushafmuslim.utils

import coil.api.load
import coil.request.CachePolicy
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.databinding.FragmentLihatFotoBinding

class LihatFotoFragment : BaseFragmentBind<FragmentLihatFotoBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_lihat_foto

    override fun myCodeHere() {
        init()
        onCLick()
    }

    private fun init(){
        bind.lifecycleOwner = this
        val urlFoto= this.arguments?.getString("urlFoto")

        bind.imgFoto.load(urlFoto) {
            crossfade(true)
            placeholder(R.drawable.ic_camera_white)
            error(R.drawable.ic_camera_white)
            fallback(R.drawable.ic_camera_white)
            memoryCachePolicy(CachePolicy.ENABLED)
        }
    }

    private fun onCLick() {
    }
}