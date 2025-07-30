package ru.steps

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permission {

    fun grantPermission(context: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Manifest.permission.ACTIVITY_RECOGNITION
            } else {
                Manifest.permission.ACTIVITY_RECOGNITION
            }

            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(context, arrayOf(permission), 1)
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }
}