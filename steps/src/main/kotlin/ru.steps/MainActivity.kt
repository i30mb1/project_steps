package ru.steps

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MainScreen(Server)
            }
        }

        Permission.grantPermission(this)
        startStepCounterWorker(this)
    }

    private fun startStepCounterWorker(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<StepCounterWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(StepCounterWorker.WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
