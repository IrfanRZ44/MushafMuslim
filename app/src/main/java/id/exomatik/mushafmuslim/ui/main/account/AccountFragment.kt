package id.exomatik.mushafmuslim.ui.main.account

import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.databinding.FragmentAccountBinding

class AccountFragment : BaseFragmentBind<FragmentAccountBinding>() {
    override fun getLayoutResource(): Int = R.layout.fragment_account
    lateinit var viewModel: AccountViewModel

    override fun myCodeHere() {
        supportActionBar?.hide()
        bind.lifecycleOwner = this
        viewModel = AccountViewModel(findNavController(), savedData, activity, context,
            bind.etUsernameReferal, bind.btnBoost)
        bind.viewModel = viewModel

        viewModel.setUpData(bind.textValidator)

        onClick()
        setUpAdmob()
    }

    private fun setUpAdmob(){
//        MobileAds.initialize(context) {}
//        val adRequest = AdRequest.Builder().build()
//        bind.adView.loadAd(adRequest)
    }

    private fun onClick() {
        bind.etUsernameReferal.editText?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onClickReferal()
                return@OnEditorActionListener false
            }
            false
        })
    }
}