package com.shurrikann.myapplication.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shurrikann.myapplication.R
import com.yalantis.ucrop.UCrop
import java.io.File

class CropHelper(
    private val activity: ComponentActivity,
    private val onCropSuccess: (Uri) -> Unit,
    private val onCropError: (Throwable?) -> Unit = {}
) {
    //构造函数重载，支持在fragment中使用
    constructor(
        fragment: Fragment,
        onCropSuccess: (Uri) -> Unit,
        onCropError: (Throwable?) -> Unit = {}
    ) :
            this(fragment.requireActivity() as ComponentActivity, onCropSuccess, onCropError)

    //注册剪裁结果发射器
    private val launcher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            result.data?.let { intent ->
                val uri = UCrop.getOutput(intent)
                if (uri != null) onCropSuccess(uri)
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            onCropError(UCrop.getError(result.data!!))
        }
    }

    //启动裁剪
    fun startCrop(sourceUri: Uri, isCircle: Boolean = true) {
        val destinationFile = File(
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "crop_${System.currentTimeMillis()}.jpg"
        )
        val destinationUri = Uri.fromFile(destinationFile)
        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)//强制1:1
            .withMaxResultSize(800, 800)

        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(90)
            //如果是圆形头像，可以在裁剪框里显示圆形覆盖层
            setCircleDimmedLayer(isCircle)
            setShowCropGrid(!isCircle)
            setShowCropFrame(!isCircle)
            //UI颜色适配
            val themeColor = ContextCompat.getColor(activity, R.color.background_color)
            setToolbarColor(themeColor) // 源码里有这个
            setToolbarWidgetColor(ContextCompat.getColor(activity, R.color.white)) // 源码里有这个
            // 源码没有 setStatusBarColor，如果想让状态栏变色，
            // 建议设置状态栏图标为暗色/亮色（取决于你的主题色）
            setStatusBarLight(false)
        }
        launcher.launch(uCrop.withOptions(options).getIntent(activity))
    }
}