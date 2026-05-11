package com.shurrikann.myapplication.application

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.shurrikann.myapplication.utils.AppConfig
import com.tencent.mmkv.MMKV
import java.util.Locale

class MyApp : Application() {

    override fun attachBaseContext(base: Context) {
        //初始化MMKV
        MMKV.initialize(base)
        //获取用户保存的语言
        val language = AppConfig.currentLanguage
        if(language.isNotEmpty()){
            val locale = Locale(language)
            Locale.setDefault(locale)
            //更新配置
            val config = Configuration(base.resources?.configuration)
            config.setLocale(locale)
            //注入
            super.attachBaseContext(base.createConfigurationContext(config))
        }else{
            //如果默认是空时 则传入原先的context 这样app就会跟随系统
            super.attachBaseContext(base)
        }
    }


    override fun onCreate() {
        super.onCreate()

    }
}