package com.shurrikann.myapplication.view

import com.shurrikann.myapplication.base.BaseActivity
import com.shurrikann.myapplication.databinding.ActivityLoginBinding
import com.shurrikann.myapplication.utils.AppConfig
import com.shurrikann.myapplication.utils.startActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private val username = "admin"
    private val password = "123456"

    override fun initView() {
        binding.apply {
            btnLogin.setOnClickListener {
                val user = binding.editUser.text.toString()
                if (!username.equals(user, ignoreCase = true)) {
                    showToast("用户名不正确")
                    return@setOnClickListener
                }
                val pwd = binding.editPwd.text.toString()
                if (pwd != password) {
                    showToast("密码不正确")
                    return@setOnClickListener
                }
                AppConfig.userLogin = true
                AppConfig.userName = user
                startActivity<MainActivity>()
                finish()
            }
        }
    }
}