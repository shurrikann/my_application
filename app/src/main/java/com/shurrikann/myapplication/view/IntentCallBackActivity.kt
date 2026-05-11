package com.shurrikann.myapplication.view

import android.content.Intent
import com.shurrikann.myapplication.base.BaseActivity
import com.shurrikann.myapplication.databinding.ActivityIntentCallbackBinding

class IntentCallBackActivity : BaseActivity<ActivityIntentCallbackBinding>(
    ActivityIntentCallbackBinding::inflate
) {
    override fun initView() {
        binding.btnCallback.setOnClickListener {
            val intent = Intent().apply {
                putExtra("extra_color", "红色")
            }
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.btnToast.setOnClickListener {
            showSnackBar("删除照片", "撤销") {
                showToast("已成功恢复")
            }
        }
    }

    override fun initData() {
        super.initData()
        //获取bundle
        val bundle = intent.extras
        //如果bundle不为空，则读取具体的值
        bundle?.let {
            val stringData = it.getString("key1") ?: "默认值"
            val intData = it.getInt("key2", -2)
            showToast("接收到的数据是:$stringData,和$intData")
        }
    }
}