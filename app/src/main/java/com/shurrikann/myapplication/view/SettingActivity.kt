package com.shurrikann.myapplication.view

import android.content.Context
import android.content.Intent
import android.view.View
import com.shurrikann.myapplication.base.BaseActivity
import com.shurrikann.myapplication.databinding.ActivitySettingBinding
import com.shurrikann.myapplication.enums.DialogType
import com.shurrikann.myapplication.utils.AppConfig

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {
    override fun initView() {
        initViewStatus()
        initListeners()
        binding.textVersion.text = getAppVersionName(this)
    }

    fun getAppVersionName(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName ?: "1.0.0"
    }

    fun getAppVersionNum(context: Context): Long {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()//旧版本返回Int，需要转换为Long
        }
    }

    //从APPconfig中读取状态并设置UI
    private fun initViewStatus() {
        binding.apply {
            switchSystemTheme.isChecked = AppConfig.isFollowSystem
            btnThemeToggle.isSelected = AppConfig.isManualDark
            //如果开启跟随系统则隐藏手动开关
            btnThemeToggle.visibility = if (AppConfig.isFollowSystem) View.GONE else View.VISIBLE
            /*
             *切换语言相关逻辑
             */
            //初始化开关状态
            switchSystemLanguage.isChecked = (AppConfig.currentLanguage == "en")
            //监听切换
            switchSystemLanguage.setOnCheckedChangeListener { _, isChecked ->
                val targetLang = if (isChecked) "en" else ""
                //判断新老语言是否一致，不一致则不处理(防止初始化触发重启)
                if (targetLang != AppConfig.currentLanguage) {
                    //MMKV自动保存新值
                    AppConfig.currentLanguage = targetLang
                    //重启App
                    //创建指向主页的intent
                    val intent = Intent(this@SettingActivity, MainActivity::class.java)
                    //清空任务栈，让APP像重新打开一样重新走一遍
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    //杀掉当前设置页并取消转场动画,让切换无感
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            btnExit.setOnClickListener {
                showCustomizationDialog(DialogType.APPEXIT, "退出应用", onConfiirm = { _, _ ->
                    AppConfig.userLogin = false
                    AppConfig.userName = "点击登录"
                    val intent = Intent(this@SettingActivity, SplashActivity::class.java)
                    //清空任务栈，让APP像重新打开一样重新走一遍
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                })
            }
            btnAppUpdate.setOnClickListener {
                
            }
        }
    }

    //监听开关操作
    private fun initListeners() {
        binding.apply {
            switchSystemTheme.setOnCheckedChangeListener { _, isChecked ->
                //直接存入AppConfig
                AppConfig.isFollowSystem = isChecked
                //执行切换
                applyAppTheme(isChecked, AppConfig.isManualDark)
                //按钮隐藏动画
                toggleBtnAnimation(isChecked)
            }
            //监听手动切换
            btnThemeToggle.setOnClickListener {
                //取反并存入AppConfig
                val newDarkState = !AppConfig.isManualDark
                AppConfig.isManualDark = newDarkState
                //更新图标
                it.isSelected = newDarkState
                //执行切换
                applyAppTheme(false, newDarkState)
            }
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun toggleBtnAnimation(hide: Boolean) {
        //先取消之前动画，避免多次点击导致冲突
        binding.btnThemeToggle.animate().cancel()
        //如果要显示，先设为可见，再跑透明度动画
        if (!hide) {
            binding.btnThemeToggle.visibility = View.VISIBLE
        }
        binding.btnThemeToggle.animate()
            .alpha(if (hide) 0f else 1f)
            .setDuration(250)
            .withEndAction {
                if (!isFinishing && !isDestroyed) {
                    //如果是隐藏，动画结束后设为GONE
                    binding.btnThemeToggle.visibility = if (hide) View.GONE else View.VISIBLE
                }
            }.start()
    }
}