package com.shurrikann.myapplication.utils

import android.Manifest
import android.os.Build

object PermissionUtil {
    //1.位置权限
    fun getAllRequiredPermissions(): Array<String> {
        val permissions = mutableListOf<String>()

        //1.固定权限
        permissions.add(android.Manifest.permission.CAMERA)
        permissions.add(android.Manifest.permission.READ_CONTACTS)
        permissions.add(android.Manifest.permission.READ_CALENDAR)
        permissions.add(android.Manifest.permission.BODY_SENSORS)
//        permissions.add(android.Manifest.permission.SEND_SMS)

        //存储权限以及新增的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Android 13+细化媒体权限
            permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(android.Manifest.permission.READ_MEDIA_VIDEO)
            permissions.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            //Android 13+新增通知权限
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
            //Android 13+新增附近设备权限
            permissions.add(android.Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            //Android 12以及以下使用通用存储权限
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissions.toTypedArray()
    }

    //获取麦克风权限
    fun getMicrophonePermission(): String {
        return android.Manifest.permission.RECORD_AUDIO
    }

    //发送短信
    fun getSmsPermissions(): Array<String> {
        val smsPermissions = mutableListOf<String>()
        smsPermissions.add(android.Manifest.permission.SEND_SMS)
        smsPermissions.add(android.Manifest.permission.READ_SMS)
        return smsPermissions.toTypedArray()
    }

    //打电话
    fun getPhonePermissions(): Array<String> {
        val phonePermissions = mutableListOf<String>()
        phonePermissions.add(android.Manifest.permission.CALL_PHONE)
        phonePermissions.add(android.Manifest.permission.READ_PHONE_STATE)
        return phonePermissions.toTypedArray()
    }

    //位置权限
    fun getLocationPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //Android 12+必须同时请求精确位置和粗略位置
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //2.相机和相册权限(Android 13+  细化了媒体权限)
    fun getCameraAndStoragePermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                //Android 13+拍照无需存储权限，但如果读取相册需要
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                //Android 11& 12
                arrayOf(Manifest.permission.CAMERA)
            }

            else -> {
                //Android 10及以下
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}