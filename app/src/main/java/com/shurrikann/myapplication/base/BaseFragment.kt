package com.shurrikann.myapplication.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.shurrikann.myapplication.R
import com.shurrikann.myapplication.enums.DialogType

abstract class BaseFragment<VB : ViewBinding>(private val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB) :
    Fragment() {

    private var _binding: VB? = null

    //子类统一使用这个binding
    val binding get() = _binding!!

    //获取宿主activity的实例
    val baseActivity: BaseActivity<*>? get() = requireActivity() as? BaseActivity<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //子类在这里写初始化逻辑
        initView()
        initData()
    }

    abstract fun initView()

    open fun initData() {

    }

    fun showLoading() {
        //可以调用BaseActivity 里的 Loading 方法
        baseActivity?.showAlertDialog()
    }

    fun showToast(message: String) {
        baseActivity?.showToast(message)
    }

    fun showSnackBar(message: String) {
        baseActivity?.showSnackBar(message)
    }

    fun showAlertDialog(
        type: DialogType = DialogType.NORMAL,
        title: String? = getString(R.string.tips),
        message: String? = "",
        confirmText: String? = null,
        cancelText: String? = null,
        autoDismissTime: Long = 0L,
        onConfiirm: (() -> Unit)? = null,
        onCacel: (() -> Unit)? = null
    ) {
        baseActivity?.showAlertDialog(
            type,
            title,
            message,
            confirmText,
            cancelText,
            autoDismissTime,
            onConfiirm = onConfiirm,
            onCacel = onCacel
        )
    }

    fun showCustomizationDialog(
        type: DialogType = DialogType.NORMAL,
        title: String? = getString(R.string.tips),
        message: String? = "",
        confirmText: String? = null,
        cancelText: String? = null,
        hint1: String? = "",
        hint2: String? = "",
        autoDismissTime: Long = 0L,
        onConfiirm: ((String, String) -> Unit)? = null,//回调带出输入内容
        onCacel: (() -> Unit)? = null
    ) {
        baseActivity?.showCustomizationDialog(
            type,
            title,
            message,
            confirmText,
            cancelText,
            hint1,
            hint2,
            autoDismissTime,
            onConfiirm = onConfiirm,
            onCacel = onCacel
        )
    }

    fun applyAppTheme(isFollowSystem: Boolean, isDark: Boolean) {
        baseActivity?.applyAppTheme(isFollowSystem, isDark)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showLoadingDialog(message: String = "正在加载...") {
        baseActivity?.showLoadingDialog(message)
    }

    fun hideLoadingDialog() {
        baseActivity?.hideLoadingDialog()
    }

}