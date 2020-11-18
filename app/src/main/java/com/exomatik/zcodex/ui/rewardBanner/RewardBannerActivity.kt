package com.exomatik.zcodex.ui.rewardBanner

import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseActivityBind
import com.exomatik.zcodex.databinding.ActivityRewardBannerBinding

class RewardBannerActivity : BaseActivityBind<ActivityRewardBannerBinding>(){
    override fun getLayoutResource(): Int = R.layout.activity_reward_banner
    lateinit var viewModel: RewardBannerViewModel

    override fun myCodeHere() {
        bind.lifecycleOwner = this
        viewModel = RewardBannerViewModel(savedData, this, bind.adView)
        bind.viewModel = viewModel
        viewModel.setUpBanner()
    }
}