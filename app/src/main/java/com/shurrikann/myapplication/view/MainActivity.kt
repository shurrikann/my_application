package com.shurrikann.myapplication.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.shurrikann.myapplication.R
import com.shurrikann.myapplication.base.BaseActivity
import com.shurrikann.myapplication.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {


    //退出应用
    private var laskBackTime: Long = 0//记录上次点击的时间
    private val finishInterval = 2000L//再次点击的间隔时间

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //导航的宿主
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // 将 BottomNavigationView 与 NavController 绑定
        // 这样点击底栏，Fragment 就会自动切换，完全不需要写 switch-case
        binding.navView?.setupWithNavController(navController)
    }

    override fun initView() {
        //注册返回键回调
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - laskBackTime > finishInterval) {
                    showToast(getString(R.string.press_again_to_exit_app))
                    laskBackTime = currentTime
                } else {
                    finishAffinity()
                }
            }
        })

    }

    override fun initData() {
        super.initData()
    }

}

