package com.example.premiumapp.core.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.premiumapp.domain.model.PermissionStatus
import com.example.premiumapp.domain.model.PermissionType

class PermissionManager(private val context: Context) {

    fun getRequiredPermissions(type: PermissionType): List<String> {
        return when (type) {
            PermissionType.CAMERA -> listOf(Manifest.permission.CAMERA)
            PermissionType.PHOTOS -> {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                        listOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                        )
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        listOf(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                    else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            PermissionType.VIDEOS -> {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                        listOf(
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                        )
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        listOf(Manifest.permission.READ_MEDIA_VIDEO)
                    }
                    else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            PermissionType.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    emptyList()
                }
            }
        }
    }

    fun checkPermissionStatus(type: PermissionType): PermissionStatus {
        val permissions = getRequiredPermissions(type)
        if (permissions.isEmpty()) return PermissionStatus.GRANTED

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        return if (allGranted) PermissionStatus.GRANTED else PermissionStatus.DENIED
    }

    fun createAppSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}
