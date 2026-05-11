package com.shurrikann.myapplication.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.shurrikann.myapplication.R
import com.shurrikann.myapplication.base.BaseActivity
import com.shurrikann.myapplication.databinding.ActivitySplashBinding
import com.shurrikann.myapplication.utils.AppConfig
import com.shurrikann.myapplication.utils.startActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private var countDownTimer: CountDownTimer? = null
    private var isJumped = false//防止重复跳转

    override fun onCreate(savedInstanceState: Bundle?) {
        //开启官方启动页支持
        val splashScreen = installSplashScreen()
        //双重保险，确保BaseActivity 渲染布局时主题已切换
        setTheme(R.style.Theme_MyApplication)
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        //开启Edge -to -edge
        window.apply {
            //允许内容绘制到状态栏和导航栏
            WindowCompat.setDecorFitsSystemWindows(this, false)
            //设置状态栏和导航栏为透明
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
            //如果是android 10以上，需要关闭导航栏默认的半透明遮罩
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isNavigationBarContrastEnforced = false
            }
        }
        //应用一次主题色保证颜色正确
        applyAppTheme(AppConfig.isFollowSystem, AppConfig.isManualDark)
        //倒计时跳转
        initCountDown()
        //点击跳转
        binding.btnSkip?.setOnClickListener {
            jumpToMain()
        }
    }


    //跳转倒计时
    private fun initCountDown() {
        //参数:总时长 3000ms 间隔1000ms
        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onFinish() {
//                binding.tvCountdown.text = "跳过0秒"
                jumpToMain()
            }

            override fun onTick(millisUntilFinished: Long) {
                //更新显示，由于是向下取整 需要+1
                val seconds = (millisUntilFinished / 1000) + 1
                binding.tvCountdown.text =
                    getString(R.string.skip) + " " + seconds + " " + getString(R.string.seconds)
            }
        }.start()
    }

    private fun jumpToMain() {
        if (isJumped) return
        isJumped = true
        //取消定时器防止内存泄漏
        countDownTimer?.cancel()
        if (AppConfig.userLogin) startActivity<MainActivity>() else startActivity<LoginActivity>()
        finish()//销毁引导页
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()//Activity 销毁时必须取消定时器
    }
}