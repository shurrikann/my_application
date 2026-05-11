package com.shurrikann.myapplication.utils

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class mmkvProperty<T>(private val key: String, private val defaultValue: T) :
    ReadWriteProperty<Any, T> {

    private val kv: MMKV by lazy { MMKV.defaultMMKV() }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when (defaultValue) {
            is Boolean -> kv.decodeBool(key, defaultValue) as T
            is Int -> kv.decodeInt(key, defaultValue) as T
            is Long -> kv.decodeLong(key, defaultValue) as T
            is Float -> kv.decodeFloat(key, defaultValue) as T
            is String -> kv.decodeString(key, defaultValue) as T
            is Parcelable -> kv.decodeParcelable(key, defaultValue.javaClass) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        when (value) {
            is Boolean -> kv.encode(key, value)
            is Int -> kv.encode(key, value)
            is Long -> kv.encode(key, value)
            is Float -> kv.encode(key, value)
            is String -> kv.encode(key, value)
            is Parcelable -> kv.encode(key, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }
}