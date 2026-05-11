package com.shurrikann.myapplication.fragment

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.shurrikann.myapplication.R
import com.shurrikann.myapplication.base.BaseFragment
import com.shurrikann.myapplication.databinding.FragmentHomeBinding
import com.shurrikann.myapplication.enums.DialogType
import com.shurrikann.myapplication.utils.launch
import com.shurrikann.myapplication.utils.startActivity
import com.shurrikann.myapplication.view.IntentCallBackActivity
import com.shurrikann.myapplication.view.PermissionsActivity

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    //跳转并回调函数,等价于startActivityforResult
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data?.getStringExtra("extra_color")
//            binding.textviewMainTitle.text = "返回结果:$data"
            showSnackBar(getString(R.string.return_result_is) + data)
        }
    }

    override fun initView() {
        binding.btnPermissions.setOnClickListener {//权限功能
            //跳转界面
            startActivity<PermissionsActivity>()//无参传递
            //有参传递
//            val data = Bundle().apply { putString("key1", "value"); putInt("key2", 1) }
//            startActivity<PermissionsActivity>(data)

        }
        binding.btnCallback.setOnClickListener {//intent回调和跳转
            //有回调的跳转
            startForResult.launch<IntentCallBackActivity>(
                requireContext(),
                "key1" to "value",
                "key2" to 1
            )
        }
        binding.btnDialogMaterial.setOnClickListener {
            showAlertDialog(
                DialogType.NORMAL,
                getString(R.string.title),
                getString(R.string.dialog_test),
                getString(R.string.confirm),
                getString(R.string.cancel),
                onConfiirm = { missionData() })
        }
        binding.btnDialogCustomization.setOnClickListener {
            showCustomizationDialog(
                DialogType.NORMAL,
                title = getString(R.string.modify_nickname),
                message = getString(R.string.input_new_nickname),
                hint1 = getString(R.string.input_new_nickname),
                confirmText = getString(R.string.modify),
                autoDismissTime = 3000L,
                onConfiirm = { text1, _ ->
                    showToast(getString(R.string.new_nickname_is) + text1)
                })
        }
    }

    //这是个测试任务
    private fun missionData() {
        showToast(getString(R.string.dialog_test))
    }
}