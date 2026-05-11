package com.shurrikann.myapplication.fragment

import com.shurrikann.myapplication.base.BaseFragment
import com.shurrikann.myapplication.databinding.FragmentProfileBinding
import com.shurrikann.myapplication.utils.startActivity
import com.shurrikann.myapplication.view.LoginActivity
import com.shurrikann.myapplication.view.ModifyInfoActivity
import com.shurrikann.myapplication.view.SettingActivity


class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    override fun initView() {
        binding.apply {
            btnSetting.setOnClickListener {
                startActivity<SettingActivity>()
            }
            clModifyUserInfo.setOnClickListener {
                startActivity<ModifyInfoActivity>()
            }
            ivAvatar.setOnClickListener {
                startActivity<LoginActivity>()
            }
        }
    }
}