package com.shurrikann.myapplication.viewmodel

import androidx.lifecycle.ViewModel

class CommonViewModel : ViewModel() {
    //用一个Map存储所有临时数据，Key为字符串 Value为任意类型
    private val dataMap = mutableMapOf<String, Any?>()

    fun <T> saveData(key: String, value: T) {
        dataMap[key] = value
    }

    fun <T> getData(key: String): T? {
        return dataMap[key] as? T
    }
}