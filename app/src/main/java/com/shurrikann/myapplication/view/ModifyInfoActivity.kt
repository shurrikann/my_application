package com.shurrikann.myapplication.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.shurrikann.myapplication.base.BaseActivity
import com.shurrikann.myapplication.databinding.ActivityModifyInfoBinding
import com.shurrikann.myapplication.mydata.UserInfo
import com.shurrikann.myapplication.utils.CropHelper
import com.shurrikann.myapplication.utils.RetrofitManager
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

class ModifyInfoActivity :
    BaseActivity<ActivityModifyInfoBinding>(ActivityModifyInfoBinding::inflate) {
    //相册选择器
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                cropHelper.startCrop(it)
            }
        }

    //照片发射器
    private var photoUri: Uri? = null
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let {
                    cropHelper.startCrop(it)
                }
            }
        }

    private lateinit var cropHelper: CropHelper
    override fun initView() {
        //必须在initview中初始化
        cropHelper = CropHelper(
            this, onCropSuccess = { uri ->
                binding.ivUserAvatar.setImageURI(uri)
                //转换为文件并上传
                val file = uriToFile(uri)
                if (file != null) {
                    uploadAvatarToServer(file)
                } else {
                    showToast("文件处理失败")
                }
            },
            onCropError = { e ->
                showToast("裁剪失败:${e?.message}")
            })
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            ivUserAvatar.setOnClickListener {
                val opens = arrayOf("拍照", "相册")
                AlertDialog.Builder(this@ModifyInfoActivity)
                    .setTitle("更换头像")
                    .setItems(opens) { _, which ->
                        when (which) {
                            0 -> checkAndRequestCameraPermission()
                            1 -> pickImageLauncher.launch("image/*")
                        }
                    }.show()
            }
            btnModifyInfo.setOnClickListener {
                val name = binding.editModifyUsername.text.toString()
                val pwd = binding.editModifyPwd.text.toString()
                if (name.isEmpty()) {
                    showToast("请输入需要修改的姓名")
                    return@setOnClickListener
                }
                if (pwd.isEmpty()) {
                    showToast("请输入需要修改的密码")
                    return@setOnClickListener
                }
                val user = UserInfo(name, pwd)

            }
        }
    }

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
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    //调用相机
    private fun openCamera() {
        // 1. 创建一个物理文件 (File)
        // 使用 getExternalCacheDir() 存放原始大图，这样图片不会出现在用户相册里，且清理缓存时会自动删掉
        val storageDir: File? = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile(
            "IMG_${System.currentTimeMillis()}_", /* 文件名前缀 */
            ".jpg",                               /* 扩展名 */
            storageDir                         /* 存放目录 */
        )

        // 2. 将 File 转换为安全协议的 Uri
        // 注意：这里的 authority 字符串必须与 AndroidManifest 中 provider 的 android:authorities 一致
        photoUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            photoFile
        )

        // 3. 启动拍照发射器
        // ActivityResultContracts.TakePicture() 内部会自动处理以下逻辑：
        // - 创建 MediaStore.ACTION_IMAGE_CAPTURE 的 Intent
        // - 自动添加 Intent.FLAG_GRANT_WRITE_URI_PERMISSION (授予相机写入该 Uri 的权限)
        // - 将 photoUri 放入 Extra (MediaStore.EXTRA_OUTPUT)
        photoUri?.let { uri ->
            takePictureLauncher.launch(uri)
        }
    }

    //拍照权限申请启动器
    private val requestCameraPermissionLauncher = registerForActivityResult(
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

    private fun gotoSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun uploadAvatarToServer(file: File) {
        //1显示加载框
        showLoadingDialog("正在上传，请稍后...")
        //2准备文件Part
        //注意avatar 必须与后端接口定义的字段名一致
        val bodyPart = RetrofitManager.prepareFilePart("avatar", file)
        //3使用协程发起请求
        lifecycleScope.launch {
            try {
                val responseBody = RetrofitManager.service.uploadFile(
                    url = "",//上传的IP地址
                    file = bodyPart,
                    params = emptyMap()
                )
                //4.解析结果，(假设后端返回的是json)
                val result = responseBody.toString()
                showToast("上传成功")
                //成功后删除临时文件
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("上传失败:${e.message}")
            } finally {
                hideLoadingDialog()
            }
        }
    }
}