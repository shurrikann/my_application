package com.shurrikann.myapplication.fragment

import android.app.Activity.RESULT_OK
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.shurrikann.myapplication.R
import com.shurrikann.myapplication.base.BaseFragment
import com.shurrikann.myapplication.databinding.FragmentHomeBinding
import com.shurrikann.myapplication.enums.DialogType
import com.shurrikann.myapplication.utils.launch
import com.shurrikann.myapplication.utils.startActivity
import com.shurrikann.myapplication.view.IntentCallBackActivity
import com.shurrikann.myapplication.view.PermissionsActivity
import com.shurrikann.myapplication.wigths.PrimaryButton
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

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
        binding.compress.setContent {
            //在这写compose的代码
            MaterialTheme {
                //获取当前屏幕方向
                PrimaryButtonList()

            }
        }
    }

    //这是个测试任务
    private fun missionData() {
        showToast(getString(R.string.dialog_test))
    }


    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun PrimaryButtonList() {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            // 水平间距（并排时按钮间的距离）
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            // 垂直间距（换行后行与行间的距离）
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // 可以限制每行显示的数量
            maxItemsInEachRow = if (isLandscape) 2 else 1
        ) {
            //计算按钮宽度
            val itemModifier = Modifier.fillMaxWidth(if (isLandscape) 0.485f else 1f)
            PrimaryButton(
                text = stringResource(id = R.string.permissions),
                onClick = {
                    startActivity<PermissionsActivity>()//无参传递
                },
                modifier = itemModifier
                    .height(45.dp)
            )
            PrimaryButton(
                text = stringResource(id = R.string.intent_callback),
                onClick = {
                    //有回调的跳转
                    startForResult.launch<IntentCallBackActivity>(
                        requireContext(),
                        "key1" to "value",
                        "key2" to 1
                    )
                },
                modifier = itemModifier
                    .height(45.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                PrimaryButton(
                    text = stringResource(id = R.string.system_style_dialog),
                    onClick = {
                        showAlertDialog(
                            DialogType.NORMAL,
                            getString(R.string.title),
                            getString(R.string.dialog_test),
                            getString(R.string.confirm),
                            getString(R.string.cancel),
                            onConfiirm = { missionData() })
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                )
                PrimaryButton(
                    text = stringResource(id = R.string.customization_style_dialog),
                    onClick = {
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
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                )
            }
            PrimaryButton(
                text = "测试使用",
                onClick = {
                    showToast("测试用")
                },
                modifier = itemModifier
                    .height(45.dp)
            )
            PrimaryButton(
                text = "测试使用",
                onClick = {
                    showToast("测试用")
                },
                modifier = itemModifier
                    .height(45.dp)
            )
            PrimaryButton(
                text = "测试使用",
                onClick = {
                    showToast("测试用")
                },
                modifier = itemModifier
                    .height(45.dp)
            )
            PrimaryButton(
                text = "测试使用",
                onClick = {
                    showToast("测试用")
                },
                modifier = itemModifier
                    .height(45.dp)
            )
            PrimaryButton(
                text = "测试使用",
                onClick = {
                    showToast("测试用")
                },
                modifier = itemModifier
                    .height(45.dp)
            )
        }
    }
}

