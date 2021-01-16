package id.exomatik.mushafmuslim.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import id.exomatik.mushafmuslim.R
import id.exomatik.mushafmuslim.databinding.FragmentMainBinding
import id.exomatik.mushafmuslim.ui.general.adapter.SectionsPagerAdapter
import id.exomatik.mushafmuslim.utils.Constant
import id.exomatik.mushafmuslim.utils.Constant.appName
import com.google.android.material.tabs.TabLayout
import id.exomatik.mushafmuslim.base.BaseFragmentBind
import id.exomatik.mushafmuslim.ui.main.account.AccountFragment
import id.exomatik.mushafmuslim.ui.main.shirah.ShirahFragment

class MainFragment : BaseFragmentBind<FragmentMainBinding>() {
    private lateinit var viewModel: MainViewModel
    override fun getLayoutResource(): Int = R.layout.fragment_main

    override fun myCodeHere() {
        supportActionBar?.hide()
        init()
    }

    private fun init() {
        bind.lifecycleOwner = this
        viewModel = MainViewModel()
        bind.viewModel = viewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager(bind.viewPager)
        bind.tabs.setupWithViewPager(bind.viewPager)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    private fun setupViewPager(pager: ViewPager) {
        val adapter = SectionsPagerAdapter(childFragmentManager)
        adapter.addFragment(ShirahFragment(), Constant.shirahNabawi)
        adapter.addFragment(AccountFragment(), Constant.account)

        pager.adapter = adapter

        bind.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        bind.tabs.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_book_white)
                        bind.tabs.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_profile_gray)
                    }
                    else -> {
                        bind.tabs.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_book_gray)
                        bind.tabs.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_profile_white)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}