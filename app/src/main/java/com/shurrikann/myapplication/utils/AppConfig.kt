package com.shurrikann.myapplication.utils

import com.tencent.mmkv.MMKV

object AppConfig {
    //获取mmkv实例
    val mmkv: MMKV by lazy { MMKV.defaultMMKV() }


    /**
     * 主题相关
     */
    var isFollowSystem by mmkvProperty("key_follow_system", true)
    var isManualDark by mmkvProperty("key_is_dark", false)

    /**
     * 语言相关
     */
    //默认zh ,开启后保存为en
    var currentLanguage by mmkvProperty("key_current_language", "")

    /**
     * 用户登录相关
     */

    //登录状态
    var userLogin by mmkvProperty("is_login", false)

    //用户姓名
    var userName by mmkvProperty("user_name", "点击登录")

    //用户token
    var userToken by mmkvProperty("user_token", "")

    //退出登录

}