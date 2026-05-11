package com.shurrikann.myapplication.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shurrikann.myapplication.R
import com.shurrikann.myapplication.enums.DialogType
import com.shurrikann.myapplication.utils.AppConfig
import java.io.File
import java.util.Locale

abstract class BaseActivity<VB : ViewBinding>(
    //工厂函数，子类传入ActivityXxxBinding::inflater即可
    private val inflater: (LayoutInflater) -> VB
) : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        //从MMKV中读取保存的语言配置
        val language = AppConfig.currentLanguage
        if (language.isNotEmpty()) {
            //创建一个指定语言的Locale对象
            val local = Locale(language)
            Locale.setDefault(local)
            //更新Configuration
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(local)
            //注入新的Context
            val context = newBase.createConfigurationContext(config)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }

    }

    /**
     * 根据传入的状态切换主题
     */
    fun applyAppTheme(isFollowSystem: Boolean, isDark: Boolean) {
        if (isFollowSystem) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            val mode =
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    /*全局的Toast引用*/
    private var mToast: Toast? = null

    //私有变量存储引用，对外暴露只读的binding
    private var _binding: VB? = null;
    val binding get() = _binding!!

    //定义共用的loading弹窗
    private val loadingDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setTitle("请稍后")
            .setMessage("正在处理中")
            .setCancelable(false)//点击外部不消失，强制等待
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflater(layoutInflater)
        setContentView(_binding?.root)
        //规范生命周期调用流程
        initView()
        initData()
    }

    //供子类实现，绑定点击事件，设置适配器等ui操作
    abstract fun initView()

    //请求网络，读取本地数据库
    open fun initData() {

    }


    override fun onDestroy() {
        super.onDestroy()
        mToast = null
        _binding = null
    }


    /**
     * 弹出Toast提示
     * @param message 文本内容
     * @param isShort 是否短时间显示(默认true)
     */
    fun showToast(message: String?, isShort: Boolean = true) {
        if (message.isNullOrEmpty()) return
        //切换主线程执行，确保在子线程调用时也不会崩溃
        runOnUiThread {
            // 取消上一个正在显示的 Toast，实现即时覆盖
            mToast?.cancel()
            //使用自定义布局
            val layout = layoutInflater.inflate(R.layout.layout_custom_toast, null)
            layout.findViewById<TextView>(R.id.tvToastMessage).text = message
            mToast = Toast(applicationContext).apply {
                view = layout
                duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
                //设置在屏幕中间偏下
                setGravity(android.view.Gravity.BOTTOM, 0, 120)
            }
            mToast?.show()
        }
    }

    /**
     * 重载方法，支持从string.xml中读取文本
     */
    fun showToast(resId: Int, isShort: Boolean = true) {
        showToast(getString(resId), isShort)
    }

    /**
     * 弹出SnackBar
     * @param message 显示的文字
     * @param actionText 按钮文字(可选)
     * @param action 按钮点击事件(可选)
     */
    fun showSnackBar(
        message: String,
        actionText: String? = null,
        duration: Int = Snackbar.LENGTH_SHORT,
        action: (() -> Unit)? = null
    ) {
        //SnackBar需要一个View来查找父布局,这里用binding.root
        val snackBar = Snackbar.make(binding.root, message, duration)
        val snackBarView = snackBar.view
        //设置背景
        snackBarView.setBackgroundResource(R.drawable.bg_snack_bar)
        //强制刷新文字颜色
        snackBar.setTextColor(ContextCompat.getColor(this, R.color.toast_text_color))
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.toast_text_color))
        if (actionText != null && action != null) {
            snackBar.setAction(actionText) {
                action()
            }
        }
        snackBar.show()
    }

    /**
     * 封装通用的 Material 对话框
     * @param title 标题
     * @param message 内容
     * @param confirmText 确定按钮文字，传 null 则不显示
     * @param cancelText 取消按钮文字，传 null 则不显示
     * @param autoDismissTime 自动消失时间（毫秒），传 0 则不自动消失
     * @param onConfirm 确定回调
     * @param onCacel 取消回调
     * */

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
        val builder = MaterialAlertDialogBuilder(this, R.style.MyDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
        var finalDismissTime = autoDismissTime
        if (type == DialogType.NORMAL) {
            if (finalDismissTime == 0L) {
                finalDismissTime = 3000L
            }
        }
        //处理确定按钮
        confirmText?.let {
            builder.setPositiveButton(it) { dialog, _ ->
                onConfiirm?.invoke()
                dialog.dismiss()
            }
        }
        //处理取消按钮
        cancelText?.let {
            builder.setNegativeButton(it) { dialog, _ ->
                onCacel?.invoke()
                dialog.dismiss()
            }
        }

        val dialog = builder.create()

        //设置自定义背景
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_snack_bar)
        if (!isFinishing) dialog.show()

        //处理自动消失
        if (finalDismissTime > 0) {
            binding.root.postDelayed({
                if (dialog.isShowing && !isFinishing) {
                    dialog.dismiss()
                }
            }, finalDismissTime)
        }
    }

    /**
     * 封装的自定义dialog
     * @param title 标题
     * @param message 内容
     * @param confirmText 确定按钮文字，传 null 则不显示
     * @param cancelText 取消按钮文字，传 null 则不显示
     * @param autoDismissTime 自动消失时间（毫秒），传 0 则不自动消失
     * @param onConfirm 确定回调
     * @param onCacel 取消回调
     * @param hint1 提示文字1
     * @param hint2 提示文字2
     */
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
        val builder = MaterialAlertDialogBuilder(this, R.style.MyDialogTheme)
//            .setTitle(title)
            .setCancelable(false)
        //动态加载布局
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom_container, null)
        val input1 = dialogView.findViewById<TextInputLayout>(R.id.inputlayout1)
        val edit1 = dialogView.findViewById<TextInputEditText>(R.id.edittext1)
        val input2 = dialogView.findViewById<TextInputLayout>(R.id.inputlayout2)
        val edit2 = dialogView.findViewById<TextInputEditText>(R.id.edittext2)
        val scroll = dialogView.findViewById<View>(R.id.scrollContainer)
        val tvContent = dialogView.findViewById<TextView>(R.id.tvLayoutContent)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvCustomTitle)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmCustom)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCustom)
        tvTitle.text = title
        var finalDismissTime = autoDismissTime
        //根据类型切换显示逻辑
        when (type) {
            DialogType.NORMAL -> {
                builder.setMessage(message)
                if (finalDismissTime == 0L) {
                    finalDismissTime = 3000L
                }
            }

            DialogType.APPEXIT -> {
                btnConfirm.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
            }

            DialogType.SINGLE_INPUT -> {
                input1.visibility = View.VISIBLE
                input1.hint = hint1
                btnConfirm.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
            }

            DialogType.DOUBLE_INPUT -> {
                input1.visibility = View.VISIBLE
                input1.hint = hint1
                input2.visibility = View.VISIBLE
                input2.hint = hint2
                btnConfirm.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
            }

            DialogType.SCROLL_TEXT -> {
                scroll.visibility = View.VISIBLE
                tvContent.text = message
                btnConfirm.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
            }
        }
        builder.setView(dialogView)
        val dialog = builder.create()

        //自定义按钮的逻辑
        btnConfirm.setOnClickListener {
            //将输入内容回调
            onConfiirm?.invoke(edit1.text.toString(), edit2.text.toString())
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            onCacel?.invoke()
            dialog.dismiss()
        }

        //如果调用系统原生按钮时
//        confirmText?.let {
//            builder.setPositiveButton(it) { dialog, _ ->
//                //将输入内容回调
//                onConfiirm?.invoke(edit1.text.toString(), edit2.text.toString())
//                dialog.dismiss()
//            }
//        }
//        cancelText?.let {
//            builder.setNegativeButton(it) { dialog, _ ->
//                onCacel?.invoke()
//                dialog.dismiss()
//            }
//        }


        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_snack_bar)
        if (!isFinishing) dialog.show()
        if (finalDismissTime > 0) {
            binding.root.postDelayed({
                if (dialog.isShowing && !isFinishing) dialog.dismiss()
            }, finalDismissTime)
        }
    }


    //添加uri转换file的方法
    fun uriToFile(uri: Uri): File? {
        val context = applicationContext
        val fileName = "temp_avatar_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    //弹窗提示
    fun showLoadingDialog(message: String = "正在加载...") {
        runOnUiThread {
            if (!isFinishing) {
                loadingDialog.setMessage(message)
                if (!loadingDialog.isShowing) {
                    loadingDialog.show()
                }
            }
        }
    }

    //隐藏弹窗的方法
    fun hideLoadingDialog(){
        runOnUiThread {
            if (loadingDialog.isShowing){
                loadingDialog.dismiss()
            }
        }
    }
}