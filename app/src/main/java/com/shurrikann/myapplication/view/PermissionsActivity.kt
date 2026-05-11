package com.shurrikann.myapplication.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.shurrikann.myapplication.base.BaseActivity
import com.shurrikann.myapplication.databinding.ActivityPermissionsBinding
import com.shurrikann.myapplication.utils.PermissionUtil
import com.shurrikann.myapplication.viewmodel.CommonViewModel
import java.io.File

class PermissionsActivity :
    BaseActivity<ActivityPermissionsBinding>(ActivityPermissionsBinding::inflate) {
    //用于保存当前拍照的文件Uri
    private var photoUri: Uri? = null

    //使用委托获取通用的ViewModel
    private val commonViewModel: CommonViewModel by viewModels()

    companion object {
        //定义唯一key
        private const val KEY_PHOTO_URI = "PERMISSION_PHOTO_URI"
    }

    //注册多权限请求回调
    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val deniedList = permissions.filter { !it.value }.keys
        if (deniedList.isEmpty()) {
            showToast("所选权限已全部授予")
        } else {
            //统一提示
            showSnackBar("部分权限被拒绝，功能受限", "去设置") {
                gotoSettings()
            }
        }
    }

    override fun initView() {
        binding.btnCamera.setOnClickListener {
            checkAndRequestCameraPermission()
        }
        binding.btnLocation.setOnClickListener {
            requestMultiplePermissions.launch(PermissionUtil.getLocationPermissions())
        }
        binding.btnMic.setOnClickListener {
            checkAndRequestAudioPermission()
        }
        binding.btnCallphone.setOnClickListener {
            requestMultiplePermissions.launch(PermissionUtil.getPhonePermissions())
        }
        binding.btnSendSms.setOnClickListener {
            requestMultiplePermissions.launch(PermissionUtil.getSmsPermissions())
        }
    }

    override fun initData() {
        val allPermissions = PermissionUtil.getAllRequiredPermissions()
        //找出哪些还没有授权
        val deniedList = allPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedList.isNotEmpty()) {
            //批量弹窗
            requestMultiplePermissions.launch(deniedList.toTypedArray())
        } else {
            showToast("所有权限已就绪")
        }
        //根据key获取保存到viewmodel的数据并强制转换类型
        val savedUriString: Uri? = commonViewModel.getData(KEY_PHOTO_URI)
        if (savedUriString != null) {
//            val uri = Uri.parse(savedUriString)
            binding.imgPreview.setImageURI(savedUriString)
            showToast("通过VM恢复照片")
        }
    }

    //1拍照启动器
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            //这里获取相机返回的缩略图
            //val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            //直接获取创建的photoUri
            photoUri?.let { uri ->
                //保存照片uri到viewmodel中
                commonViewModel.saveData(KEY_PHOTO_URI, photoUri)
                binding.imgPreview.setImageURI(uri)
                showSnackBar("原图已保存")
            }
            //缩略图显示在img上
            //binding.imgPreview.setImageBitmap(imageBitmap)
        }
    }

    //拍照权限申请启动器
    private val requestCarmeraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            showSnackBar("没有相机权限,无法拍照", "去设置") {
                //引导用户去系统设置界面
                gotoSettings()
            }
        }
    }

    //检查并申请拍照权限
    private fun checkAndRequestCameraPermission() {
        when {
            //A 已有权限的情况
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            //B 系统认为需要向用户解释
            else -> {
                requestCarmeraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    //调用相机
    private fun openCamera() {
        //创建临时文件用来保存高清图片
        val photoFile = File(externalCacheDir, "IMG_${System.currentTimeMillis()}.jpg")
        //通过fileprovider 将file转换为安全的uri
        photoUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            //告诉相机，拍好的照片存到Uri指向的位置
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            //授予相机临时读写该Uri的权限
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        //确保设备有相机应用响应这个intent
        // 注意：Android 11+ 如果没有在 Manifest 配置 <queries>，resolveActivity 可能返回 null
        if (intent.resolveActivity(packageManager) != null) {
            takePictureLauncher.launch(intent)
        } else {
            try {
                takePictureLauncher.launch(intent)
            } catch (e: Exception) {
                showToast("未找到相机应用")
            }
        }
        //告诉系统相册扫描新文件夹并加入相册数据库
        MediaScannerConnection.scanFile(this, arrayOf(photoFile.absolutePath), null) { path, uri ->
            Log.d("scan", "相册已更新:$path")
        }
    }

    //跳转到设置的方法
    private fun gotoSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    //麦克风权限启动
    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startRecording()//获取权限，开始录音
        } else {
            gotoSettings()
        }
    }

    //检查录音权限
    private fun checkAndRequestAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording()
        } else {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    //获取麦克风权限后具体业务逻辑
    private fun startRecording() {
        showToast("已经获取麦克风权限")
    }

}