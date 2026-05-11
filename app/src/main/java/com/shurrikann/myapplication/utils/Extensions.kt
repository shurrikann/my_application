package com.shurrikann.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * 1. 键值对版本 (最常用)
 * 用法：startActivity<SettingsActivity>("id" to 1, "name" to "shurrikann")
 */
inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    startActivity(intent)
}

/**
 * 2. Bundle 版本 (处理复杂对象或已有 Bundle)
 * 用法：val b = Bundle(); b.putParcelable("user", user); startActivity<DetailActivity>(b)
 */
inline fun <reified T : Activity> Context.startActivity(bundle: Bundle?) {
    val intent = Intent(this, T::class.java)
    bundle?.let {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

/**
 * 3. 针对 Fragment 的快捷扩展 (可选)
 * 让你在 Fragment 里少写一个 "requireContext()."
 * 用法：startActivity<SettingsActivity>()
 */
inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any>) {
    requireContext().startActivity<T>(*params)
}

inline fun <reified T : Activity> Fragment.startActivity(bundle: Bundle?) {
    requireContext().startActivity<T>(bundle)
}

/**
 * 专门为 ActivityResultLauncher 写的扩展，支持键值对传参
 */
inline fun <reified T : Activity> ActivityResultLauncher<Intent>.launch(
    context: Context,
    vararg params: Pair<String, Any?>
) {
    val intent = Intent(context, T::class.java).apply {
        if (params.isNotEmpty()) {
            putExtras(bundleOf(*params))
        }
    }
    this.launch(intent)
}

/**
 * 专门处理对象的委托
 */

inline fun <reified T : Any> mmkvObject(key: String, default: T) =
    object : ReadWriteProperty<Any, T> {
        private val mmkv = MMKV.defaultMMKV()
        private val gson = Gson()

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            val json = mmkv.decodeString(key,"")
            return if(json.isNullOrEmpty()) default else gson.fromJson(json,T::class.java)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            mmkv.encode(key,gson.toJson(value))
        }
    }